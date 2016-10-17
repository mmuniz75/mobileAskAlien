package edu.muniz.universeguide.mobile;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {

    private Integer questionId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getIntent().getIntExtra(Constants.QUESTION_ID,0);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_feed_back);


    }

    public void sendFeedback(View view) {

        EditText editText = (EditText)findViewById(R.id.commentsText);
        String comments = editText.getText().toString();

        if(comments.length()==0){
            String mensagem = getString(R.string.msg_no_feedback);
            Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            new FeedBackTask().execute();
            String mensagem = getString(R.string.feedback_message);
            Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
            toast.show();
            onBackPressed();
        }
    }

    public void cancel(View view) {
        onBackPressed();
    }

    private class FeedBackTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String url = "http://" + Constants.SERVER +"/rest/question/feedback";

                EditText editText = (EditText)findViewById(R.id.nameText);
                String name = editText.getText().toString();

                editText = (EditText)findViewById(R.id.emailText);
                String email = editText.getText().toString();

                editText = (EditText)findViewById(R.id.commentsText);
                String comments = editText.getText().toString();

                Map<String, String> data = new HashMap<String, String>();
                data.put("questionId", Integer.toString(questionId));
                data.put("name",name);
                data.put("email",email);
                data.put("comments",comments);

                HttpRequest.post(url).form(data).created();

                return null;
            } catch (Exception e) {
                Log.e(getPackageName(), e.getMessage(), e);
                return null;
            }
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
