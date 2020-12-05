package com.ndong.hwapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionErrorFragment extends Fragment {
  private ActivityCallback callback;

  public ConnectionErrorFragment() {
    // Required empty public constructor
  }

  public interface ActivityCallback {
    void handleNewHostname(String newHost);
  }

  public void setActivityCallback(ActivityCallback callback){
    this.callback = callback;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_connection_error, container, false);
    EditText edtNewHost = view.findViewById(R.id.edtNewHost);
    Button btnOk = view.findViewById(R.id.btnOk);
    btnOk.setOnClickListener(v -> {
      FragmentActivity context = getActivity();
      String host = edtNewHost.getText().toString();
      if (context != null) context.getSupportFragmentManager().beginTransaction().remove(this).commit();
      callback.handleNewHostname(host);
    });
    Button btnCancel = view.findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(v -> {
      Activity context = getActivity();
      if (context != null) context.finish();
    });
    return view;
  }
}