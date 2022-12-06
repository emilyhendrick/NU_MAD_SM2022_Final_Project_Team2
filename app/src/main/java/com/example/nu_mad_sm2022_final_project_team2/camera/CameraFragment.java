package com.example.nu_mad_sm2022_final_project_team2.camera;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider = null;
    private int lensFacingBack;
    private int lensFacingFront;
    private int lensFacing;

    private IPhotoTaken mListener;

    private FloatingActionButton takePhotoButton;
    private FloatingActionButton switchCameraButton;
    private FloatingActionButton openGalleryButton;

    public CameraFragment() {}

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lensFacingBack = CameraSelector.LENS_FACING_BACK;
        lensFacingFront = CameraSelector.LENS_FACING_FRONT;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        BottomNavigationView navView = view.findViewById(R.id.nav_view);
        navView.setVisibility(View.GONE);

        previewView = view.findViewById(R.id.preview);

        takePhotoButton = view.findViewById(R.id.takePhotoButton);
        switchCameraButton = view.findViewById(R.id.switchCameraButton);
        openGalleryButton = view.findViewById(R.id.openGalleryButton);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lensFacing = lensFacing == lensFacingBack ? lensFacingFront : lensFacingBack;
                setUpCamera(lensFacing);
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOpenGalleryPressed();
            }
        });

        lensFacing = lensFacingBack;
        setUpCamera(lensFacing);
        return view;
    }

    private void setUpCamera(int lensFacing) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(()->{
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();
            try {
                cameraProvider = cameraProviderFuture.get();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) getContext(),cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void takePhoto() {
        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(
                getContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();


        imageCapture.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        mListener.onPhotoTaken(outputFileResults.getSavedUri());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IPhotoTaken){
            mListener = (IPhotoTaken) context;
        }else{
            throw new RuntimeException(context+" must implement DisplayTakenPhoto");
        }
    }

    public interface IPhotoTaken {
        void onPhotoTaken(Uri imageUri);
        void onOpenGalleryPressed();
    }
}