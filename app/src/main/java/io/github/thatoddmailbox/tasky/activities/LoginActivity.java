package io.github.thatoddmailbox.tasky.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.thatoddmailbox.tasky.AuthManager;
import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.api.APIClient;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_webview)
    WebView loginWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log in");
        ButterKnife.bind(this);

        loginWebview.setWebViewClient(new LoginWebViewClient());
        WebSettings webSettings = loginWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        loginWebview.loadUrl(APIClient.getRequestAuthURL());
    }

    @Override
    public void onBackPressed() {
        if (loginWebview.copyBackForwardList().getCurrentIndex() > 0) {
            loginWebview.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri u = Uri.parse(url);
            if (u.getHost().contains("myhomework.space")) {
                // it's a MyHomeworkSpace site, let it load
                if (url.startsWith("https://stuff.myhomework.space/taskyCallbackHelper.html")) {
                    // it's the callback url, handle the token
                    String token = u.getQueryParameter("token");
                    Log.i("tasky", token);
                    AuthManager.setToken(getBaseContext(), token);
                    LoginActivity.this.finish();
                }
                return false;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}