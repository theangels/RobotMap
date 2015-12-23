package ubibots.robotmap;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by TheAngels on 2015/12/23.
 */
public class Control {
    private String dest = null;
    private boolean requireFinish = false;

    public String getDest() {
        return dest;
    }

    public boolean isRequireFinish() {
        return requireFinish;
    }

    public void setRequireFinish(boolean isRequireFinish) {
        this.requireFinish = isRequireFinish;
    }

    public void requirePlace() {
        final EditText mEditText = new EditText(MapsActivity.mContext);
        AlertDialog.Builder mDialog = new AlertDialog.Builder(MapsActivity.mContext);
        mDialog.setTitle("请输入目的地");
        mDialog.setView(mEditText);
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dest = mEditText.getText().toString();
                        requireFinish = true;
                    }
                });
        mDialog.setNegativeButton("取消", null);
        mDialog.create().show();
    }
}
