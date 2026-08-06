package com.comikey.downloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Keeps the Comikey login inside Android WebView.
 *
 * Deliberately there is no JavaScript bridge and no cookie export. The WebView
 * owns its session locally; future downloader code can make authenticated
 * requests from this same WebView/profile without exposing raw cookies.
 */
public class MainActivity extends Activity {
    private static final String COMIKEY_LOGIN = "https://comikey.com/login";
    private WebView webView;
    private ProgressBar progressBar;
    private TextView title;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildScreen();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setUserAgentString(settings.getUserAgentString() + " ComikeyDownloader/1.0");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                if (host != null && (host.equals("comikey.com") || host.endsWith(".comikey.com"))) {
                    return false;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(progress >= 100 ? View.GONE : View.VISIBLE);
                title.setText(view.getTitle() == null || view.getTitle().isEmpty()
                        ? "Login do Comikey"
                        : view.getTitle());
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl(COMIKEY_LOGIN);
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(16, 17, 25));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(18, 12, 12, 10);
        toolbar.setBackgroundColor(Color.rgb(16, 17, 25));

        Button back = makeButton("‹");
        back.setOnClickListener(v -> {
            if (webView.canGoBack()) webView.goBack();
        });
        toolbar.addView(back, new LinearLayout.LayoutParams(44, 44));

        title = new TextView(this);
        title.setText("Login do Comikey");
        title.setTextColor(Color.rgb(242, 238, 229));
        title.setTextSize(16);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, 44, 1);
        titleParams.setMargins(10, 0, 8, 0);
        toolbar.addView(title, titleParams);

        Button clear = makeButton("Sair");
        clear.setOnClickListener(v -> confirmClearSession());
        toolbar.addView(clear, new LinearLayout.LayoutParams(64, 44));
        root.addView(toolbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(249, 199, 79)));
        root.addView(progressBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 3));

        FrameLayout webContainer = new FrameLayout(this);
        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        webContainer.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(webContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(root);
    }

    private Button makeButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(Color.rgb(249, 199, 79));
        button.setTextSize(12);
        button.setAllCaps(false);
        button.setBackgroundColor(Color.TRANSPARENT);
        return button;
    }

    private void confirmClearSession() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta")
                .setMessage("Isso apagará a sessão local do WebView neste aparelho.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sair", (dialog, which) -> {
                    CookieManager.getInstance().removeAllCookies(value -> {
                        CookieManager.getInstance().flush();
                        webView.clearCache(true);
                        webView.clearHistory();
                        webView.loadUrl(COMIKEY_LOGIN);
                    });
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}