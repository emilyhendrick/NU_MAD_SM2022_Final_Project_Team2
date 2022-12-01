package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {

    private static final String ARG_ALARMS = "alarmsarray";

    private ImageView alarmBackArrow;
    private TextView addAlarmButton;
    private RecyclerView alarmRecyclerView;
    private RecyclerView.LayoutManager alarmRecyclerViewLayoutManager;

    private AlarmsAdaptor alarmsAdaptor;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IAlarmFragmentAction mListener;

    private ArrayList<Alarm> mAlarms;

    public AlarmFragment() {}

    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALARMS, new ArrayList<Alarm>());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_ALARMS)) {
                mAlarms = (ArrayList<Alarm>) args.getSerializable(ARG_ALARMS);
            }

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            loadData();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAlarmFragmentAction) {
            this.mListener = (IAlarmFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // SET UP RECYCLER VIEW
        alarmsAdaptor = new AlarmsAdaptor(mAlarms, getContext());
        alarmRecyclerView = view.findViewById(R.id.alarmRecyclerView);
        alarmRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        alarmRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        alarmRecyclerView.setLayoutManager(alarmRecyclerViewLayoutManager);
        alarmRecyclerView.setAdapter(alarmsAdaptor);

        addAlarmButton = view.findViewById(R.id.alarmPlus);
        addAlarmButton.setClickable(true);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addAlarmClicked();
            }
        });

        alarmBackArrow = view.findViewById(R.id.alarmLeftArrow);
        alarmBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.alarmBackArrowClicked();
            }
        });


        // Listener for Firebase data changes
        db.collection("users")
                        .document(mUser.getEmail())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    User user = value.toObject(User.class);
                                    updateRecyclerView(user.getAlarms());
                                    mAlarms = user.getAlarms();
                                    mUser.reload();
                                }
                            }
                        });


        return view;
    }

    public void updateRecyclerView(ArrayList<Alarm> alarms) {
        this.mAlarms = alarms;
        alarmsAdaptor.setAlarms(alarms);
        alarmsAdaptor.notifyDataSetChanged();
    }

    private void loadData() {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            updateRecyclerView(user.getAlarms());
                            mAlarms = user.getAlarms();
                            mUser.reload();
                        }
                    }
                });
    }

    public interface IAlarmFragmentAction {
        void alarmBackArrowClicked();
        void addAlarmClicked();
    }
}