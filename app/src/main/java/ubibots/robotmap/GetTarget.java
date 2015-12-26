package ubibots.robotmap;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class GetTarget {
    private String dest = null;

    public String getDest() {
        return dest;
    }

    public void requirePlace() {
        final EditText mEditText = new EditText(MapsActivity.context);
        AlertDialog.Builder mDialog = new AlertDialog.Builder(MapsActivity.context);
        mDialog.setTitle("请输入目的地");
        mDialog.setView(mEditText);
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dest = mEditText.getText().toString();
                        Flag.requireFinish = true;
                    }
                });
        mDialog.setNegativeButton("取消", null);
        mDialog.create().show();
    }
}
