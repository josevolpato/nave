package io.github.josevolpato.nave;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences prefs;
		prefs = getSharedPreferences("HiScore", MODE_PRIVATE);

		final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
		final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

		buttonPlay.setOnClickListener(this);

		long fastestTime = prefs.getLong("fastestTime", 10000000);
		textFastestTime.setText("Recorde:" + fastestTime);
	}

	@Override
	public void onClick(View v)
	{
		Intent i = new Intent(this, GameActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		return false;
	}
}