package com.tehilafi.ama;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>( MainActivity.class );

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_BACKGROUND_LOCATION" );

    @Test
    public void mainActivityTest() {
        ViewInteraction button = onView(
                allOf( withId( R.id.requesr_location_updates_button ), withText( "UPDATE LOCATION" ),
                        childAtPosition(
                                childAtPosition(
                                        withClassName( is( "android.widget.RelativeLayout" ) ),
                                        1 ),
                                0 ),
                        isDisplayed() ) );
        button.perform( click() );

        ViewInteraction button2 = onView(
                allOf( withId( R.id.remove_location_updates_button ), withText( "REMOVE UPDATES" ),
                        childAtPosition(
                                childAtPosition(
                                        withClassName( is( "android.widget.RelativeLayout" ) ),
                                        1 ),
                                1 ),
                        isDisplayed() ) );
        button2.perform( click() );

        ViewInteraction searchAutoComplete = onView(
                allOf( withClassName( is( "android.widget.SearchView$SearchAutoComplete" ) ),
                        childAtPosition(
                                allOf( withClassName( is( "android.widget.LinearLayout" ) ),
                                        childAtPosition(
                                                withClassName( is( "android.widget.LinearLayout" ) ),
                                                1 ) ),
                                0 ),
                        isDisplayed() ) );
        searchAutoComplete.perform( replaceText( "nu" ), closeSoftKeyboard() );

        ViewInteraction searchAutoComplete2 = onView(
                allOf( withClassName( is( "android.widget.SearchView$SearchAutoComplete" ) ), withText( "nu" ),
                        childAtPosition(
                                allOf( withClassName( is( "android.widget.LinearLayout" ) ),
                                        childAtPosition(
                                                withClassName( is( "android.widget.LinearLayout" ) ),
                                                1 ) ),
                                0 ),
                        isDisplayed() ) );
        searchAutoComplete2.perform( click() );

        ViewInteraction searchAutoComplete3 = onView(
                allOf( withClassName( is( "android.widget.SearchView$SearchAutoComplete" ) ), withText( "nu" ),
                        childAtPosition(
                                allOf( withClassName( is( "android.widget.LinearLayout" ) ),
                                        childAtPosition(
                                                withClassName( is( "android.widget.LinearLayout" ) ),
                                                1 ) ),
                                0 ),
                        isDisplayed() ) );
        searchAutoComplete3.perform( replaceText( "n" ) );

        ViewInteraction searchAutoComplete4 = onView(
                allOf( withClassName( is( "android.widget.SearchView$SearchAutoComplete" ) ), withText( "n" ),
                        childAtPosition(
                                allOf( withClassName( is( "android.widget.LinearLayout" ) ),
                                        childAtPosition(
                                                withClassName( is( "android.widget.LinearLayout" ) ),
                                                1 ) ),
                                0 ),
                        isDisplayed() ) );
        searchAutoComplete4.perform( closeSoftKeyboard() );

        ViewInteraction searchAutoComplete5 = onView(
                allOf( withClassName( is( "android.widget.SearchView$SearchAutoComplete" ) ), withText( "n" ),
                        childAtPosition(
                                allOf( withClassName( is( "android.widget.LinearLayout" ) ),
                                        childAtPosition(
                                                withClassName( is( "android.widget.LinearLayout" ) ),
                                                1 ) ),
                                0 ),
                        isDisplayed() ) );
        searchAutoComplete5.perform( click() );
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText( "Child at position " + position + " in parent " );
                parentMatcher.describeTo( description );
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches( parent )
                        && view.equals( ((ViewGroup) parent).getChildAt( position ) );
            }
        };
    }
}
