package kasidis.suthison.com.findfriend.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kasidis.suthison.com.findfriend.R;

public class FriendAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> nameStringArrayList, pathUrlStringArrayList;

    public FriendAdapter(Context context, ArrayList<String> nameStringArrayList, ArrayList<String> pathUrlStringArrayList) {
        this.context = context;
        this.nameStringArrayList = nameStringArrayList;
        this.pathUrlStringArrayList = pathUrlStringArrayList;
    }

    @Override
    public int getCount() {
        return nameStringArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.listview_friend, viewGroup, false);

        TextView textView = view.findViewById(R.id.txtDisplayName);
        textView.setText(nameStringArrayList.get(i));

        CircleImageView circleImageView = view.findViewById(R.id.imvAvatar);
        Picasso.get().load(pathUrlStringArrayList.get(i)).into(circleImageView);

        return view;
    }
}
