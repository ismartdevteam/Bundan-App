package ismartdev.mn.bundan.util;

/**
 * Created by Ulzii on 11/13/2016.
 */


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * This class is a generic way of backing an RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type.
 * <p>
 * To use this class in your app, subclass it passing in all required parameters and implement the
 * populateViewHolder method.
 * <p>
 * <pre>
 *     private static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
 *         TextView messageText;
 *         TextView nameText;
 *
 *         public ChatMessageViewHolder(View itemView) {
 *             super(itemView);
 *             nameText = (TextView)itemView.findViewById(android.R.id.text1);
 *             messageText = (TextView) itemView.findViewById(android.R.id.text2);
 *         }
 *     }
 *
 *     FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> adapter;
 *     DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
 *
 *     RecyclerView recycler = (RecyclerView) findViewById(R.id.messages_recycler);
 *     recycler.setHasFixedSize(true);
 *     recycler.setLayoutManager(new LinearLayoutManager(this));
 *
 *     adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessage.class, android.R.layout.two_line_list_item, ChatMessageViewHolder.class, ref) {
 *         public void populateViewHolder(ChatMessageViewHolder chatMessageViewHolder, ChatMessage chatMessage, int position) {
 *             chatMessageViewHolder.nameText.setText(chatMessage.getName());
 *             chatMessageViewHolder.messageText.setText(chatMessage.getMessage());
 *         }
 *     };
 *     recycler.setAdapter(mAdapter);
 * </pre>
 *
 * @param <T>  The Java class that maps to the type of objects stored in the Firebase location.
 * @param <VH> The ViewHolder class that contains the Views in the layout that is shown for each object.
 */
public abstract class RecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    protected int mModelLayout;
    Class<T> mModelClass;
    Class<VH> mViewHolderClass;
    HashMap<String, T>  mSnapshots;

    RecyclerAdapter(Class<T> modelClass,
                    int modelLayout,
                    Class<VH> viewHolderClass,
                    HashMap<String, T>  snapshots) {
        mModelClass = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
        mSnapshots = snapshots;

    }

    public void cleanup() {
        mSnapshots.clear();
    }


    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

//    public T getItem(int position) {
//        return mSnapshots.;
//    }




    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
//        T model = getItem(position);
//        populateViewHolder(viewHolder, model, position,mSnapshots.getItem(position).getKey());
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }

    abstract protected void populateViewHolder(VH viewHolder, T model, int position,String key);
}