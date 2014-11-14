package rumi.zulucoding.com.rumi;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Functions {
     
    private Context mContext;
     
    public Functions(Context context){

        this.mContext = context;
    }
    
    // Toast Message (Long)
    public void toast(String message) 
	{
		Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG)
		.show();
	}
    
    // Get Device Information 
    public String getDeviceName() {
    	  String manufacturer = Build.MANUFACTURER;
    	  String model = Build.MODEL;
    	  if (model.startsWith(manufacturer)) {
    	    return upperCaseFirst(model);
    	  } else {
    	    return upperCaseFirst(manufacturer) + " " + model;
    	  }
    	}
    
    // Capitalize
	public String upperCaseFirst(String text) {
		char first = text.charAt(0);
		if (text == null || text.length() == 0) {
		    return "Unknow";
		}
		else if (Character.isUpperCase(first)) {
		    return text;
		} else {
		    return Character.toUpperCase(first) + text.substring(1);
		}
	}

    // Random Function
    public int randomizeInRange(int range){
        Random randomGenerator = new Random();
        int random_number = randomGenerator.nextInt(range);
        return random_number;
    }

    public static Drawable getAssetImagePng(Context context, String filename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = new BufferedInputStream((assets.open("img/" + filename + ".png")));
        Bitmap bitmap = BitmapFactory.decodeStream(buffer);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable getAssetImageJpg(Context context, String filename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = new BufferedInputStream((assets.open("img/" + filename + ".jpg")));
        Bitmap bitmap = BitmapFactory.decodeStream(buffer);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

}