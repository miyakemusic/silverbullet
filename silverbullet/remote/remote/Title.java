package jp.silverbullet.remote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
public @interface Title {
	enum Layout {
		Normal,
		LeftHalf,
		RightHalf
	}
	public String caption();

	public int height() default 1;
	
	public Layout layout() default Layout.Normal;

	public int width() default 700;
}
