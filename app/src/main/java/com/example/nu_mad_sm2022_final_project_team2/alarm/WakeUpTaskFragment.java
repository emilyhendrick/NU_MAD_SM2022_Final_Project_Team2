package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nu_mad_sm2022_final_project_team2.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WakeUpTaskFragment extends Fragment {

    private static final String WORDLE_LINK = "https://www.nytimes.com/games/wordle/index.html";
    private static final String MATH_LINK = "https://www.mathopolis.com/questions/day.php";
    private static final String GUESS_THE_MOVIE_LINK = "https://framed.wtf/";
    private static final String GUESS_THE_SONG_LINK = "https://www.heardle.app/";
    private static final String JIGSAW_PUZZLE_LINK = "https://games.usatoday.com/en/games/daily-jigsaw";
    private static final String CROSSWORD_LINK = "https://www.latimes.com/games/mini-crossword";

    private Map<WakeUpTask, String> wakeUpTaskLinkMap = new HashMap<>();

    private TextView wakeUpTaskLink;
    private ImageView wakeUpBackButton;

    private IWakeUpTaskFragmentAction mListener;

    public WakeUpTaskFragment() {}

    public static WakeUpTaskFragment newInstance() {
        WakeUpTaskFragment fragment = new WakeUpTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IWakeUpTaskFragmentAction) {
            this.mListener = (IWakeUpTaskFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_up_task, container, false);

        wakeUpTaskLinkMap.put(WakeUpTask.WORDLE, WORDLE_LINK);
        wakeUpTaskLinkMap.put(WakeUpTask.MATH, MATH_LINK);
        wakeUpTaskLinkMap.put(WakeUpTask.GUESS_THE_MOVIE, GUESS_THE_MOVIE_LINK);
        wakeUpTaskLinkMap.put(WakeUpTask.GUESS_THE_SONG, GUESS_THE_SONG_LINK);
        wakeUpTaskLinkMap.put(WakeUpTask.JIGSAW_PUZZLE, JIGSAW_PUZZLE_LINK);
        wakeUpTaskLinkMap.put(WakeUpTask.CROSSWORD, CROSSWORD_LINK);

        wakeUpTaskLink = view.findViewById(R.id.wakeUpTaskLink);
        wakeUpBackButton = view.findViewById(R.id.wakeUpTaskLeftArrow);

        wakeUpTaskLink.setClickable(true);
        wakeUpTaskLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WakeUpTask wakeUpTask = getRandomWakeUpTask();
                String linkStr = wakeUpTaskLinkMap.get(wakeUpTask);
                mListener.wakeUpTaskLinkClicked(linkStr);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(linkStr));
//                startActivity(intent);
            }
        });

        wakeUpBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.wakeUpBackButtonClicked();
            }
        });

        return view;
    }

    private WakeUpTask getRandomWakeUpTask() {
        Random random = new Random();
        int randomTaskIndex = random.nextInt(WakeUpTask.values().length);
        return getWakeUpTasksAtIndex(randomTaskIndex);
    }

    private WakeUpTask getWakeUpTasksAtIndex(int index) {
        switch (index) {
            case 0: return WakeUpTask.WORDLE;
            case 1: return WakeUpTask.MATH;
            case 2: return WakeUpTask.GUESS_THE_MOVIE;
            case 3: return WakeUpTask.JIGSAW_PUZZLE;
            case 4: return WakeUpTask.CROSSWORD;
            default: throw new IllegalArgumentException("No WakeUpTask found for the given index");
        }
    }

    public interface IWakeUpTaskFragmentAction {
        void wakeUpBackButtonClicked();
        void wakeUpTaskLinkClicked(String linkStr);
    }
}