package com.cheezycode.notesample.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheezycode.notesample.R;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.ArrayList;
import java.util.List;


public class faq extends Fragment {

    private static String[] faq_questions, faq_answers;
    RecyclerView recyclerView;

    public faq() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        faq_questions = getResources().getStringArray(R.array.faq_questions);
        faq_answers = getResources().getStringArray(R.array.faq_answers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final RecyclerAdapter adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

        private final List<Object> list;

        private final ExpansionLayoutCollection expansionsCollection;

        public RecyclerAdapter() {
            this.list = new ArrayList<>();
            for (int i = 0; i < faq_questions.length; i++) {
                list.add(new FaqItem(faq_questions[i], faq_answers[i]));
            }
            expansionsCollection = new ExpansionLayoutCollection();
            expansionsCollection.openOnlyOne(true);
        }

        @NonNull
        @Override
        public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return RecyclerHolder.buildFor(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
                holder.bind(list.get(position));
                holder.setTextViews(faq_questions[position], faq_answers[position]);
                expansionsCollection.add(holder.getExpansionLayout());

        }

        public static class FaqItem {
            String question;
            String answer;

            public FaqItem(String question, String answer) {
                this.question = question;
                this.answer = answer;
            }

            public String getQuestion() {
                return question;
            }

            public String getAnswer() {
                return answer;
            }
        }



        @Override
        public int getItemCount() {
            return faq_questions.length;
        }


        public void setItems(List<Object> items) {
            this.list.addAll(items);
            notifyDataSetChanged();
        }

        public static class RecyclerHolder extends RecyclerView.ViewHolder {

            private static final int LAYOUT = R.layout.expansion_cell_recycler_panel;

            ExpansionLayout expansionLayout;
            TextView question_text, answer_text;

            public RecyclerHolder(View itemView) {
                super(itemView);
                expansionLayout = itemView.findViewById(R.id.expansionLayout);
                question_text = itemView.findViewById(R.id.question_text);
                answer_text = itemView.findViewById(R.id.answer_text);
            }

            public static RecyclerHolder buildFor(ViewGroup viewGroup) {
                return new RecyclerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(LAYOUT, viewGroup, false));
            }

            public void bind(Object object) {
                expansionLayout.collapse(false);
            }

            public ExpansionLayout getExpansionLayout() {
                return expansionLayout;
            }

            public void setTextViews(String ques, String ans) {
                question_text.setText(ques);
                answer_text.setText(ans);
            }
        }
    }
}
