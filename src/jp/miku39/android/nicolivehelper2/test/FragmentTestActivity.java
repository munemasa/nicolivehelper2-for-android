package jp.miku39.android.nicolivehelper2.test;

import jp.miku39.android.nicolivehelper2.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FragmentTestActivity extends Activity {
	
	int mCount;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmenttest);

		mCount = 0;

		Button btn = (Button) findViewById(R.id.btn_tabswipte_test);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addFragment(v);
			}
		});
		
	}

	void addFragment(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Fragment newFragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString("label", ""+mCount);
        newFragment.setArguments(args);

        ft.replace(R.id.fragmenttest_container, newFragment);
		ft.addToBackStack(null);
        ft.commit();
		mCount++;
	}

}
