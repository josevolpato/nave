package io.github.josevolpato.nave;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable
{
	volatile boolean playing;
	Thread gameThread = null;

	private int screenX;
	private int screenY;

	private PlayerShip player;
	private EnemyShip enemy1;
	private EnemyShip enemy2;
	private EnemyShip enemy3;
	private EnemyShip enemy4;
	private EnemyShip enemy5;
	private ArrayList<SpaceDust> dustList = new ArrayList<>();

	private Canvas canvas;
	private Paint paint;
	private SurfaceHolder ourHolder;

	private float distanceRemaining;
	private long timeTaken;
	private long timeStarted;
	private long fastestTime;

	private Context context;

	private boolean gameEnded;
	private boolean gameFinished;

	private SoundPool soundPool;
	int start = -1;
	int bump = -1;
	int destroyed = -1;
	int win = -1;

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;


	public TDView(Context context, int x, int y)
	{
		super(context);
		this.context = context;

		prefs = context.getSharedPreferences("Melhor pontuação", context.MODE_PRIVATE);
		editor = prefs.edit();

		fastestTime = prefs.getLong("fastestTime", 10000000);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		try
		{
			AssetManager assetManager = context.getAssets();
			AssetFileDescriptor descriptor;
			descriptor = assetManager.openFd("start.ogg");
			start = soundPool.load(descriptor, 0);
			descriptor = assetManager.openFd("win.ogg");
			win = soundPool.load(descriptor, 0);
			descriptor = assetManager.openFd("bump.ogg");
			bump = soundPool.load(descriptor, 0);
			descriptor = assetManager.openFd("destroyed.ogg");
			destroyed = soundPool.load(descriptor, 0);

		}
		catch (IOException ex)
		{
			Log.e("error", "failed to load sound files");
		}

		ourHolder = getHolder();
		paint = new Paint();
		screenX = x;
		screenY = y;
		startGame();
	}

	@Override
	public void run()
	{
		while (playing)
		{
			update();
			draw();
			control();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent)
	{
		switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_UP:
				player.stopBoosting();
				break;

			case MotionEvent.ACTION_DOWN:
				player.setBoosting();
				if (gameEnded)
				{
					startGame();
				}
				break;
		}
		return true;
	}

	private void update()
	{
		boolean hitDetected = false;
		if (Rect.intersects(player.getHitBox(), enemy1.getHitBox()))
		{
			hitDetected = true;
			enemy1.setX(-200);
		}

		if (Rect.intersects(player.getHitBox(), enemy2.getHitBox()))
		{
			hitDetected = true;
			enemy2.setX(-200);
		}

		if (Rect.intersects(player.getHitBox(), enemy3.getHitBox()))
		{
			hitDetected = true;
			enemy3.setX(-200);
		}

		if (screenX > 1000)
		{
			if (Rect.intersects(player.getHitBox(), enemy4.getHitBox()))
			{
				hitDetected = true;
				enemy4.setX(-200);
			}
		}

		if (screenX > 1200)
		{
			if (Rect.intersects(player.getHitBox(), enemy5.getHitBox()))
			{
				hitDetected = true;
				enemy5.setX(-200);
			}
		}

		if (hitDetected)
		{
			soundPool.play(bump, 1, 1, 0, 0, 1);
			player.reduceShieldStrength();
			if (player.getShieldStrength() < 0)
			{
				soundPool.play(destroyed, 1, 1, 0, 0, 1);
				gameEnded = true;
				gameFinished = false;
			}
		}

		player.update();
		enemy1.update(player.getSpeed());
		enemy2.update(player.getSpeed());
		enemy3.update(player.getSpeed());
		if (screenX > 1000)
		{
			enemy4.update(player.getSpeed());
		}
		if (screenX > 1200)
		{
			enemy5.update(player.getSpeed());
		}
		for (SpaceDust sd : dustList)
		{
			sd.update(player.getSpeed());
		}

		if (!gameEnded)
		{
			distanceRemaining -= player.getSpeed();
			timeTaken = System.currentTimeMillis() - timeStarted;
		}

		if (distanceRemaining < 0)
		{
			soundPool.play(win, 1, 1, 0, 0, 1);
			if (timeTaken < fastestTime)
			{
				editor.putLong("fastestTime", timeTaken);
				editor.commit();
				fastestTime = timeTaken;
			}

			distanceRemaining = 0;

			gameEnded = true;
			gameFinished = true;
		}
	}

	private void draw()
	{
		if (ourHolder.getSurface().isValid())
		{
			canvas = ourHolder.lockCanvas();
			canvas.drawColor(Color.argb(255, 0, 0, 0));
			paint.setColor(Color.argb(255, 255, 255, 255));
			for (SpaceDust sd : dustList)
			{
				// Dust 2 x 2
				canvas.drawPoint(sd.getX(), sd.getY(), paint);
				canvas.drawPoint(sd.getX() + 1, sd.getY(), paint);
				canvas.drawPoint(sd.getX(), sd.getY() + 1, paint);
				canvas.drawPoint(sd.getX() + 1, sd.getY() + 1, paint);
			}

            /*
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawRect(player.getHitBox().left, player.getHitBox().top, player.getHitBox().right, player.getHitBox().bottom, paint);
            canvas.drawRect(enemy1.getHitBox().left, enemy1.getHitBox().top, enemy1.getHitBox().right, enemy1.getHitBox().bottom, paint);
            canvas.drawRect(enemy2.getHitBox().left, enemy2.getHitBox().top, enemy2.getHitBox().right, enemy2.getHitBox().bottom, paint);
            canvas.drawRect(enemy3.getHitBox().left, enemy3.getHitBox().top, enemy3.getHitBox().right, enemy3.getHitBox().bottom, paint);
            */

			canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
			canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
			canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
			canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
			if (screenX > 1000)
			{
				canvas.drawBitmap(enemy4.getBitmap(), enemy4.getX(), enemy4.getY(), paint);
			}

			if (screenX > 1200)
			{
				canvas.drawBitmap(enemy5.getBitmap(), enemy5.getX(), enemy5.getY(), paint);
			}

			if (!gameEnded)
			{
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(Color.argb(255, 255, 255, 255));
				paint.setTextSize(25);
				canvas.drawText("Recorde: " + formatTime(fastestTime) + "s", 10, 20, paint);
				canvas.drawText("Tempo: " + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
				canvas.drawText("Distância: " + distanceRemaining / 1000 + " Km", screenX / 3, screenY - 20, paint);
				canvas.drawText("Escudo: " + player.getShieldStrength(), 10, screenY - 20, paint);
				canvas.drawText("Velocidade: " + player.getSpeed() * 60 + " Mps", (screenX / 3) * 2, screenY - 20, paint);
			}
			else
			{
				paint.setTextSize(80);
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(gameFinished ? "Parabéns!" : "Game Over", screenX / 2, 100, paint);
				paint.setTextSize(25);
				canvas.drawText("Recorde: " + formatTime(fastestTime) + "s", screenX / 2, 160, paint);
				canvas.drawText("Tempo: " + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
				canvas.drawText("Distância restante: " + distanceRemaining/1000 + " Km", screenX / 2, 240, paint);
				paint.setTextSize(80);
				canvas.drawText("Toque para jogar novamente!", screenX / 2, 350, paint);
			}

			ourHolder.unlockCanvasAndPost(canvas);
		}
	}

	private void control()
	{
		try
		{
			gameThread.sleep(17);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}
	}

	public void pause()
	{
		playing = false;
		try
		{
			gameThread.join();
		}
		catch (InterruptedException ex)
		{

		}
	}

	public void resume()
	{
		playing = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	private void startGame()
	{
		player = new PlayerShip(context, screenX, screenY);
		enemy1 = new EnemyShip(context, screenX, screenY);
		enemy2 = new EnemyShip(context, screenX, screenY);
		enemy3 = new EnemyShip(context, screenX, screenY);

		if (screenX > 1000)
		{
			enemy4 = new EnemyShip(context, screenX, screenY);
		}

		if (screenX > 1200)
		{
			enemy5 = new EnemyShip(context, screenX, screenY);
		}

		int numSpecs = 40;
		dustList = new ArrayList<>();
		for (int i = 0; i < numSpecs; i++)
		{
			SpaceDust spec = new SpaceDust(screenX, screenY);
			dustList.add(spec);
		}

		distanceRemaining = 10000;
		timeTaken = 0;
		timeStarted = System.currentTimeMillis();

		gameEnded = false;
		soundPool.play(start, 1, 1, 0, 0, 1);
	}

	private String formatTime(long time)
	{
		long seconds = (time) / 1000;
		long thousandths = (time) - (seconds * 1000);
		String strThousandths = "" + thousandths;
		if (thousandths < 100) { strThousandths = "0" + thousandths; }
		if (thousandths < 10) { strThousandths = "0" + strThousandths; }
		return "" + seconds + "." + strThousandths;
	}

}
