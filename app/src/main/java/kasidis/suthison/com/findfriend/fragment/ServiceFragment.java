package kasidis.suthison.com.findfriend.fragment;

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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kasidis.suthison.com.findfriend.MainActivity;
import kasidis.suthison.com.findfriend.R;
import kasidis.suthison.com.findfriend.util.FriendAdapter;
import kasidis.suthison.com.findfriend.util.UserModel;

public class ServiceFragment extends Fragment {

    private String displayNameString, uidUserLoggedInString;
    private ArrayList<String> uidFriendStringArrayList, friendStringArrayList, pathAvatarStringArrayList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

//        Find Member of Friend
        findMember();

    }// main method

    private void findMember() {
        uidFriendStringArrayList = new ArrayList<>();
        friendStringArrayList = new ArrayList<>();
        pathAvatarStringArrayList = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Log.d("inn",dataSnapshot1.toString());
                    if(!uidUserLoggedInString.equals(dataSnapshot1.getKey())){
                        uidFriendStringArrayList.add(dataSnapshot1.getKey());
                    }
                }//for
                Log.d("inn","uidFriend ==> " + uidFriendStringArrayList.toString());

                createListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createListView() {

        final int [] ints = new int[]{0};


        for(int i=0;i<uidFriendStringArrayList.size();i++){

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child(uidFriendStringArrayList.get(i));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("inn","datasnapshot >> "+ dataSnapshot.toString());
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);

                    friendStringArrayList.add(userModel.getNameString());
                    pathAvatarStringArrayList.add(userModel.getPathAvataString());

                    if (ints[0] == (uidFriendStringArrayList.size()-1)) {
                        Log.d("inn","Friend >> "+ friendStringArrayList.toString());
                        Log.d("inn","Path >> "+ pathAvatarStringArrayList.toString());

                        FriendAdapter friendAdapter = new FriendAdapter(getActivity(), friendStringArrayList, pathAvatarStringArrayList);
                        ListView listView = getView().findViewById(R.id.listViewFriend);
                        listView.setAdapter(friendAdapter);

                    }
                 ints[0] ++;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }// for



    }// create listview

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemSignOut) {
            signOutFirebase();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void signOutFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentMainFragment, new MainFragment())
                .commit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_service, menu);
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbarService);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My All Friends");

//        Show displayName on Subtitle Toolbar
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        displayNameString = firebaseUser.getDisplayName();
        uidUserLoggedInString = firebaseUser.getUid();

        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(displayNameString + " has Signed in");

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        return view;
    }
}
