package backgroundclasses;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by guptapc on 17/02/18.
 */

public class CurrencyBackground extends IntentService {

    public CurrencyBackground() {
        super("CurrencyBackground");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("MyTestService", "Service running");
        //new getJSONValue(rootContext).execute(AppURL+watchlistURL).get();
    }
}
