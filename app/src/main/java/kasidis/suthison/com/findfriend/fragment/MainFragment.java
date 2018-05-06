package kasidis.suthison.com.findfriend.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import kasidis.suthison.com.findfriend.R;
import kasidis.suthison.com.findfriend.util.MyAlert;

public class MainFragment extends Fragment{

    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Check Status
        checkStatus();
//        Register Controller
        registerController();
//        SignIn Controller
        signInController();
    }//Main Method

    private void signInController() {
        Button button = getView().findViewById(R.id.btnSignIn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Please Wait ...");
                progressDialog.show();

                EditText emailEditText = getView().findViewById(R.id.edtEmail);
                EditText passwordEditText = getView().findViewById(R.id.edtPassword);

                String emailString = emailEditText.getText().toString().trim();
                String passwordString = passwordEditText.getText().toString().trim();

                MyAlert myAlert = new MyAlert(getActivity());

                if(emailString.isEmpty()||passwordString.isEmpty()){
                    //Have Space
                    myAlert.normalDialog(getString(R.string.title_space)
                            ,getString(R.string.message_space));

                    progressDialog.dismiss();

                }else{
                    //No Space
                    checkEmailAndPass(emailString, passwordString);
                }
            }
        });
    }

    private void checkEmailAndPass(String emailString, String passwordString) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
//                    Sign In True

                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contentMainFragment, new ServiceFragment())
                            .commit();
                    progressDialog.dismiss();
                }
                else{
//                    Sign in false
                    MyAlert myAlert = new MyAlert(getActivity());
                    myAlert.normalDialog("Cannot Sign in"
                            ,"because => "+task.getException().getMessage().toString());

                    progressDialog.dismiss();

                }
            }
        });

    }

    private void checkStatus() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentMainFragment, new ServiceFragment())
                    .commit();

        }
    }

    private void registerController() {
        TextView textView = getView().findViewById(R.id.txtRegister);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Replace Fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentMainFragment, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();


            }
        });
    }
}
