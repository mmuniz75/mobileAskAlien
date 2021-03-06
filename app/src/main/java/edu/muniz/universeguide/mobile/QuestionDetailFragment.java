package edu.muniz.universeguide.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;




/**
 * A fragment representing a single Question detail screen.
 * This fragment is either contained in a {@link QuestionListActivity}
 * in two-pane mode (on tablets) or a {@link QuestionDetailActivity}
 * on handsets.
 */
public class QuestionDetailFragment extends Fragment {


    private Integer questionId;
    private QuestionDetailFragment instance;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuestionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
   }

    private View rootView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.question_detail, container, false);

        new GetQuestionTask().execute();

        return rootView;
    }

    private class GetQuestionTask extends AsyncTask<String, Void, String[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Wait");
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String id = getArguments().getString(Constants.ANSWER_ID);

                String url = Constants.SERVER +"/answer/" + id;

                String search = getArguments().getString(Constants.ASK);


                HttpRequest request = HttpRequest.get(url, true, "question", search);

                String conteudo = request.body();

                JSONObject questionObject = new JSONObject(conteudo);

                String[] answer = new String[1];

                //JSONObject questionObject = (JSONObject)jsonObject.get("answer");
                String number = questionObject.getString("id");
                String question = questionObject.getString("question");
                String content = questionObject.getString("content");
                String date = questionObject.getString("date");
                questionId = questionObject.getInt("questionId");
                getActivity().getIntent().putExtra(Constants.QUESTION_ID, questionId);

                answer[0] = number + Constants.FIELDS_SPLITER + question + Constants.FIELDS_SPLITER + content + Constants.FIELDS_SPLITER + date;
                return answer;

            } catch (Exception e) {
                Log.e(getActivity().getPackageName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                Map<String, String> answerMap = getAnswer(result[0]);

                Activity activity = instance.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(getString(R.string.question) + " " + answerMap.get("number"));
                }

                String formatedContent = Html.fromHtml(answerMap.get("content")).toString();
                ((TextView) rootView.findViewById(R.id.question_detail)).setText(formatedContent);

                ((TextView) rootView.findViewById(R.id.question_date)).setText(answerMap.get("date"));

            }
            dialog.dismiss();
        }

        private Map<String, String> getAnswer(String result) {

            Map<String, String> question = new HashMap<String, String>();;
            StringTokenizer token = new StringTokenizer(result,Constants.FIELDS_SPLITER);
            question.put("number", token.nextToken());
            question.put("question", token.nextToken());
            question.put("content", token.nextToken());
            question.put("date", token.nextToken());

            return question;
        }

    }
}
