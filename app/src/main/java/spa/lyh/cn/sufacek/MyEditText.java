package spa.lyh.cn.sufacek;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private OnCancel mOnCancel;

    public MyEditText(@NonNull Context context) {
        super(context);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mOnCancel != null) {
            mOnCancel.onCancel();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public void setOnCancelListener(OnCancel mOnCancel) {
        this.mOnCancel = mOnCancel;
    }

    public interface OnCancel {
        void onCancel();
    }

}
