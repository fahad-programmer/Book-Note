package com.cheezycode.notesample.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cheezycode.notesample.databinding.NoteItemBinding;
import com.cheezycode.notesample.models.NoteResponse;
import com.cheezycode.notesample.databinding.TrashItemBinding;
import com.cheezycode.notesample.utils.TokenManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrashNoteAdapter extends ListAdapter<NoteResponse, TrashNoteAdapter.TrashNoteViewHolder> {

    @Inject
    TokenManager tokenManager;
    String token;

    private final OnNoteSelectedListener listener;

    protected TrashNoteAdapter(Context context, OnNoteSelectedListener listener) {
        super(new ComparatorDiffUtil());
        this.tokenManager = new TokenManager(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrashNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TrashItemBinding binding = TrashItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TrashNoteViewHolder(binding);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull TrashNoteViewHolder holder, int position) {
        NoteResponse note = getItem(position);
        holder.bind(note);
        holder.itemView.setOnClickListener(view -> {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("note_id", note.getId())
                                .add("token", Objects.requireNonNull(tokenManager.getToken()))
                                .build();

                        System.out.println(note.getId());
                        System.out.println(tokenManager.getToken());
                        Request request = new Request.Builder()
                                .url("https://unexpectedprogrammer.pythonanywhere.com/api/notes/" + note.getId() + "/restore/")
                                .addHeader("Authorization", "Token " + tokenManager.getToken())
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        return response.isSuccessful();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        // Handle the response
                        listener.onNoteSelected(note);
                    } else {
                        Toast.makeText(view.getContext(), "Failed to restore note", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        });
    }



    public interface OnNoteSelectedListener {
        void onNoteSelected(NoteResponse note);
    }

    public static class TrashNoteViewHolder extends RecyclerView.ViewHolder {
        private final TrashItemBinding binding;

        public TrashNoteViewHolder(TrashItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NoteResponse note) {
            binding.title.setText(note.getTitle());
            binding.desc.setText(note.getBody());
            binding.dateOfDelete.setText(String.format("Deleted At: %s", note.getDeleted_at()));
        }
    }

    private static class ComparatorDiffUtil extends DiffUtil.ItemCallback<NoteResponse> {
        @Override
        public boolean areItemsTheSame(@NonNull NoteResponse oldItem, @NonNull NoteResponse newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NoteResponse oldItem, @NonNull NoteResponse newItem) {
            return oldItem.equals(newItem);
        }
    }
}