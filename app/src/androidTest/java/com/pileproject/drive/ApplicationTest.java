package com.pileproject.drive;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	public void setUp() {
		Context c = new DelegatedMockContext(getContext());
	}
	public ApplicationTest() {
		super(Application.class);
    }
}