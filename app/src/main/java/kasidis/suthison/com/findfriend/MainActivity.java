package kasidis.suthison.com.findfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import kasidis.suthison.com.findfriend.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentMainFragment,new MainFragment())
                    .commit();
        }

    }   //Main Method

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Cannot Undo", Toast.LENGTH_SHORT).show();
    }
}//Main Class
