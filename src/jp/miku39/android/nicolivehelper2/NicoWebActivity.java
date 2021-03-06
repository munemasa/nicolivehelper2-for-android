package jp.miku39.android.nicolivehelper2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.miku39.android.nicolivehelper2.fragments.AboutDialogFragment;
import jp.miku39.android.nicolivehelper2.fragments.InputDialogFragment;
import jp.miku39.android.nicolivehelper2.libs.Lib;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

public class NicoWebActivity extends Activity implements
		InputDialogFragment.Callback {
	final static String TAG = "NicoWebActivity";
	// final static String sFirstUri =
	// "https://secure.nicovideo.jp/secure/login_form";
	final static String sFirstUri = "http://sp.live.nicovideo.jp/my";
	// final static String sFirstUri = "http://live.nicovideo.jp/";
	private WebView mWeb;

	class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			ProgressBar bar = (ProgressBar) findViewById(R.id.webloading_progressbar);
			bar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			ProgressBar bar = (ProgressBar) findViewById(R.id.webloading_progressbar);
			bar.setVisibility(View.GONE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, url);
			Pattern p = Pattern.compile("watch/((lv|co|ch)\\d+)");
			Matcher m = p.matcher(url);
			if (m.find()) {
				// 生放送の視聴へ
				final String lvid = m.group(1);
				Intent intent = new Intent(NicoWebActivity.this,
						NicoLiveHelperMainActivity.class);
				intent.putExtra("lvid", lvid);
				startActivity(intent);
			} else {
				Lib.setStringValue(NicoWebActivity.this, "last-url", url);
				return super.shouldOverrideUrlLoading(view, url);
			}
			return true;
		}
	}

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nicoweb);

		mWeb = (WebView) findViewById(R.id.loginweb);
		mWeb.getSettings().setJavaScriptEnabled(true);
		mWeb.getSettings().setPluginsEnabled(true);
		mWeb.getSettings().setBuiltInZoomControls(true);
		mWeb.getSettings().setLoadWithOverviewMode(true);
		mWeb.getSettings().setUseWideViewPort(true);
		mWeb.setWebViewClient(new MyWebViewClient());

		String str = Lib.getStringValue(this, "last-url");
		if (str.length() > 0) {
			mWeb.loadUrl(str);
		} else {
			mWeb.loadUrl(sFirstUri);
		}

		initButtons();
	}

	private void initButtons() {
		Button btn;
		btn = (Button) findViewById(R.id.btn_webprev);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWeb.canGoBack()) {
					mWeb.goBack();
				}
			}
		});
		btn = (Button) findViewById(R.id.btn_webforward);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWeb.canGoForward()) {
					mWeb.goForward();
				}
			}
		});
		btn = (Button) findViewById(R.id.btn_webtop);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWeb.loadUrl("http://sp.live.nicovideo.jp/");
			}
		});
		btn = (Button) findViewById(R.id.btn_webmypage);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWeb.loadUrl("http://sp.live.nicovideo.jp/my");
			}
		});
	}

	/**
	 * 放送IDを入力して生放送に接続
	 */
	void connectLive() {
		String caption = "放送IDの入力";
		String text = "接続したい放送IDを入力してください。";

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		DialogFragment newFragment = InputDialogFragment.newInstance(caption,
				text);
		newFragment.show(ft, "dialog");
	}
	
	/**
	 * 番組の新規作成ページに飛ぶだけ
	 */
	void createLive(){
		mWeb.loadUrl("http://live.nicovideo.jp/editstream");
	}

	@Override
	public void onReturnValue(InputDialogFragment frag, String str) {
		Intent intent = new Intent(NicoWebActivity.this,
				NicoLiveHelperMainActivity.class);
		intent.putExtra("lvid", str);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nicowebmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// メニュー
		switch (item.getItemId()) {
		default:
			super.onOptionsItemSelected(item);
			break;
		case R.id.menu_connect_direct:
			// 放送IDを入力して接続
			connectLive();
			break;
			
		case R.id.menu_create_live:
			createLive();
			break;

		case R.id.menu_about:
			NicoLiveHelperMainActivity.openAboutDialog(this);
			break;
		}
		return true;
	}


}
