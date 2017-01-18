package ismartdev.mn.bundan.util;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.Query;

/**
 * Created by Ulzii on 11/13/2016.
 */
public abstract class FirebaseIndexRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends FirebaseRecyclerAdapter<T, VH> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param keyRef          The Firebase location containing the list of keys to be found in {@code dataRef}.
     *                        Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param dataRef         The Firebase location to watch for data changes.
     *                        Each key key found at {@code keyRef}'s location represents a list item in the {@code RecyclerView}.
     */
    public FirebaseIndexRecyclerAdapter(Class<T> modelClass,
                                        int modelLayout,
                                        Class<VH> viewHolderClass,
                                        Query keyRef,
                                        Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, new FirebaseIndexArray(keyRef, dataRef));
    }
}