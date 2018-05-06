package kasidis.suthison.com.findfriend.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import kasidis.suthison.com.findfriend.MainActivity;
import kasidis.suthison.com.findfriend.R;
import kasidis.suthison.com.findfriend.util.MyAlert;
import kasidis.suthison.com.findfriend.util.UserModel;

public class RegisterFragment extends Fragment {

    private String nameString, emailString, paswordString, pathAvatarString, uidUserString;
    private Uri uri;
    private ImageView imageView;
    private Boolean chooseBool = true;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

//        Avatar Controller
        avatarController();

    }// Main Method

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){

            uri = data.getData();
            chooseBool = false;

            try{
                Bitmap bitmap = BitmapFactory
                        .decodeStream(getActivity()
                                .getContentResolver()
                                .openInputStream(uri));
                imageView.setImageBitmap(bitmap);

            }catch(Exception e){
                e.printStackTrace();
            }

        }// if
    }// OnActivityResult

    private void avatarController() {
        imageView = getView().findViewById(R.id.imvAvatar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent
                        ,"Please Choose Image Application")
                        ,100);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_register, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemUploadValue) {

            // TODO

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait ...");
            progressDialog.show();

            checkTextField();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkTextField() {
//        Get Value From EditText
        EditText edtName = getView().findViewById(R.id.edtName);
        EditText edtEmail = getView().findViewById(R.id.edtEmail);
        EditText edtPassword = getView().findViewById(R.id.edtPassword);

        nameString = edtName.getText().toString();
        emailString = edtEmail.getText().toString();
        paswordString = edtPassword.getText().toString();

        MyAlert myAlert = new MyAlert(getActivity());

        if(chooseBool == true){
            //Haven't choosed image yet
            myAlert.normalDialog("You didn't choose image","Please choose your avatar");
            progressDialog.dismiss();
        }
        else if(nameString.isEmpty()
                || emailString.isEmpty()
                || paswordString.isEmpty()){
//            Have Space
            myAlert.normalDialog(getString(R.string.title_space),getString(R.string.message_space));
            progressDialog.dismiss();
        }
        else{
//            No Space
            uploadValueToFirebase();
        }

    }

    private void uploadValueToFirebase() {
        // upload Image
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference storageReference1 = storageReference.child("Avatar/"
                + nameString
                +"Avatar");
        storageReference1.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("inn", "Upload Image Success");

                    findPathAvatar();

                }else{
                    Log.d("inn", "Upload Image Fail because >> " + task.getException().getMessage().toString());
                    progressDialog.dismiss();
                }
            }
        });

    } //Upload Value

    private void findPathAvatar() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        final String[] strings = new String[1];
        storageReference.child("Avatar/" + nameString +"Avatar")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        strings[0] = uri.toString();
                        pathAvatarString = strings[0];
                        Log.d("inn" ,"Path Avatar ==>" +pathAvatarString);
                        registerEmail();
                    }
                });
    }

    private void registerEmail() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, paswordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("inn","Register Success");
                            findUidUser();
                        }else{
                            Log.d("inn","register fail");
                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Register fail"
                                    ,task.getException().getMessage().toString());

                        }
                    }
                });

    }

    private void findUidUser(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        uidUserString = firebaseUser.getUid();
        Log.d("inn","uidUser ==> "+ uidUserString);

//        Setup DisplayName
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(nameString);

        UserProfileChangeRequest userProfileChangeRequest = builder.build();
        firebaseUser.updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateNewUserToFirebase();
            }
        });


    }

    private void updateNewUserToFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(uidUserString);

        UserModel userModel = new UserModel(nameString, pathAvatarString);

        databaseReference.setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("inn", "Success Update");
                progressDialog.dismiss();

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentMainFragment, new ServiceFragment())
                        .commit();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("inn", "Cannot Update ==> "+ e.toString());
            }
        });
    }


    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.new_register));
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please Fill All Blank");
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .popBackStack();
            }
        });//click back icon

        setHasOptionsMenu(true);

    }// create tool bar
}
