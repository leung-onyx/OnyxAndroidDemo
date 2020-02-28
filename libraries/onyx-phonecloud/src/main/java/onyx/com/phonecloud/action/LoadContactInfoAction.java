package onyx.com.phonecloud.action;

import android.support.annotation.NonNull;
import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.JSONUtils;

import java.util.List;

import onyx.com.phonecloud.model.Contact;
import onyx.com.phonecloud.request.LoadContactInfoRequest;

/**
 * Created by TonyXie on 2020-02-28
 */
public class LoadContactInfoAction extends BasePhoneCloudStoreAction {

    @Override
    public void execute(RxCallback callback) {
        LoadContactInfoRequest loadContactInfoRequest = new LoadContactInfoRequest();
        getCloudNoteManager().enqueue(loadContactInfoRequest, new RxCallback<LoadContactInfoRequest>() {
            @Override
            public void onNext(@NonNull LoadContactInfoRequest loadContactInfoRequest) {
                List<Contact> contacts = loadContactInfoRequest.getContacts();
                String data = JSONUtils.toJson(contacts);
                Log.e("tony", data);
            }
        });
    }
}
