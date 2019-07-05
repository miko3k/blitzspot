package org.deletethis.blitzspot.app.fragments.choose;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;

import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseFragment extends Fragment {
    private final MyAdapter adapter = new MyAdapter();
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (androidx.recyclerview.widget.RecyclerView) inflater.inflate(
                R.layout.choose_fragment,
                container,
                false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    public void setDataSet(DataSet dataSet) {
        adapter.setData(dataSet);
    }

    public void setMessage(String message) {
        Consumer<View> bind = view ->
                ((TextView)view.findViewById(R.id.text)).setText(message);

        recyclerView.setAdapter(new SingleViewAdapter(R.layout.choose_message, bind));
    }

}
