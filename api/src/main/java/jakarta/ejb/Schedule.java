/*
 * Copyright (c) 2006, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.ejb;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Schedule a timer for automatic creation with a timeout schedule based on a cron-like time expression. The annotated
 * method is used as the timeout callback method.
 * <p>
 * All elements of this annotation are optional. If none are specified a persistent timer will be created with callbacks
 * occuring every day at midnight in the default time zone associated with the container in which the application is
 * executing.
 * <p>
 * There are seven elements that constitute a schedule specification which are listed below. In addition, the
 * <code>timezone</code> element may be used to specify a non-default time zone in whose context the schedule
 * specification is to be evaluated; the <code>persistent</code> element may be used to specify a non-persistent timer,
 * and the <code>info</code> element may be used to specify additional information that may be retrieved when the timer
 * callback occurs.
 * <p>
 * The elements that specify the calendar-based schedule itself are as follows:
 *
 * <ul>
 * <li>second : one or more seconds within a minute
 * <p>
 * Allowable values: [0,59]
 *
 * <li>minute : one or more minutes within an hour
 * <p>
 * Allowable values : [0,59]
 *
 * <li>hour : one or more hours within a day
 * <p>
 * Allowable values : [0,23]
 *
 * <li>dayOfMonth : one or more days within a month
 * <p>
 * Allowable values:
 * <ul>
 * <li>[1,31]
 * <li>[-7, -1]
 * <li>"Last"
 * <li>{"1st", "2nd", "3rd", "4th", "5th", "Last"} {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}
 * </ul>
 * <p>
 * "Last" means the last day of the month
 * <p>
 * -x (where x is in the range [-7, -1]) means x day(s) before the last day of the month
 * <p>
 * "1st","2nd", etc. applied to a day of the week identifies a single occurrence of that day within the month.
 *
 * <li>month : one or more months within a year
 * <p>
 * Allowable values :
 *
 * <ul>
 * <li>[1,12]
 * <li>{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", Dec"}
 * </ul>
 *
 * <li>dayOfWeek : one or more days within a week
 * <p>
 * Allowable values :
 *
 * <ul>
 * <li>[0,7]
 * <li>{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}
 * </ul>
 * <p>
 * "0" and "7" both refer to Sunday
 *
 * <li>year : a particular calendar year
 * <p>
 * Allowable values : a four-digit calendar year
 *
 * <p>
 * </ul>
 * <p>
 *
 * Each element supports values expressed in one of the following forms
 *
 * <ul>
 * <li>Single Value. This constrains the attribute to only one of its possible values.
 *
 * <pre>
 *
 * Example: second = "10"
 * Example: month = "Sep"
 * </pre>
 *
 * <li>Wild Card. "*" represents all allowable values for a given attribute.
 *
 * <pre>
 * Example: second = "*"
 * Example: dayOfWeek = "*"
 * </pre>
 *
 * <li>List. This constrains the attribute to two or more allowable values or ranges, with a comma used as a separator
 * character within the string. Each item in the list must be a single value or range. List items cannot be lists, wild
 * cards, or increments. Duplicate values are ignored.
 *
 * <pre>
 * Example: second = "10,20,30"
 * Example: dayOfWeek = "Mon,Wed,Fri"
 * Example: minute = "0-10,30,40"
 * </pre>
 *
 * <li>Range. This constrains the attribute to an inclusive range of values, with a dash separating both ends of the
 * range. Each side of the range must be a single attribute value. Members of a range cannot be lists, wild cards,
 * ranges, or increments. If <code>x</code> is larger than <code>y</code> in a range <code>"x-y"</code>, the range is
 * equivalent to <code>"x-max, min-y"</code>, where <code>max</code> is the largest value of the corresponding attribute
 * and <code>min</code> is the smallest. The range <code>"x-x"</code>, where both range values are the same, evaluates
 * to the single value <code>x</code>. The day of the week range <code>"0-7"</code> is equivalent to <code>"*"</code>.
 *
 *
 * <pre>
 * Example: second = "1-10"
 * Example: dayOfWeek = "Fri-Mon"
 * Example: dayOfMonth = "27-3" (Equivalent to "27-Last , 1-3")
 * </pre>
 *
 * <li>Increments. The forward slash constrains an attribute based on a starting point and an interval, and is used to
 * specify every <code>N</code> seconds, minutes, or hours within the minute, hour, or day, respectively. For the
 * expression <code>x/y</code>, the attribute is constrained to every <code>y</code>th value within the set of allowable
 * values beginning at time <code>x</code>. The <code>x</code> value is inclusive. The wild card character
 * (<code>*</code>) can be used in the <code>x</code> position, and is equivalent to <code>0</code>. The use of
 * increments is only supported within the <code>second</code>, <code>minute</code>, and <code>hour</code> elements. For
 * the <code>second</code> and <code>minute</code> elements, <code>x</code> and <code>y</code> must each be in the range
 * <code>[0,59]</code>. For the <code>hour</code> element, <code>x</code> and <code>y</code> must each be in the range
 * <code>[0,23]</code>.
 *
 *
 * <pre>
 * Example: minute = "&#8727;/5" (Every five minutes within the hour)
 * </pre>
 *
 * This is equivalent to: <code>minute = "0,5,10,15,20,25,30,35,40,45,50,55"</code>
 *
 *
 * <pre>
 * Example: second = "30/10" (Every 10 seconds within the minute, starting at second 30)
 * </pre>
 *
 * This is equivalent to: <code>second = "30,40,50"</code>
 * <p>
 * Note that the set of matching increment values stops once the maximum value for that attribute is exceeded. It does
 * not "roll over" past the boundary.
 *
 *
 * <pre>
 * Example : ( minute = "&#8727;/14", hour="1,2")
 * </pre>
 * <p>
 * This is equivalent to: <code>(minute = "0,14,28,42,56", hour = "1,2")</code> (Every 14 minutes within the hour, for
 * the hours of 1 and 2 a.m.)
 * </ul>
 *
 * <p>
 * The following additional rules apply to the schedule specification elements:
 * <ul>
 * <li>If the <code>dayOfMonth</code> element has a non-wildcard value and the <code>dayOfWeek</code> element has a
 * non-wildcard value, then any day matching either the <code>dayOfMonth</code> value or the <code>dayOfWeek</code>
 * value will be considered to apply.
 * <li>Whitespace is ignored, except for string constants and numeric values.
 * <li>All string constants (e.g., <code>"Sun"</code>, <code>"Jan"</code>, <code>"1st"</code>, etc.) are case
 * insensitive.
 * </ul>
 * <p>
 * Schedule-based timer times are evaluated in the context of the default time zone associated with the container in
 * which the application is executing. A schedule-based timer may optionally override this default and associate itself
 * with a specific time zone. If the schedule-based timer is associated with a specific time zone, all its times are
 * evaluated in the context of that time zone, regardless of the default time zone in which the container is executing.
 * <p>
 * The timeout callback method to which the <code>Schedule</code> annotation is applied must have one of the following
 * signatures, where <code>&lt;METHOD&gt;</code> designates the method name:
 *
 *
 * <pre>
 * void &#060;METHOD&#062;()
 * void &#060;METHOD&#062;(Timer timer)
 * </pre>
 *
 * A timeout callback method can have public, private, protected, or package level access. A timeout callback method
 * must not be declared as final or static. Timeout callback methods must not throw application exceptions.
 *
 * @since EJB 3.1
 */
@Target(METHOD)
@Retention(RUNTIME)
@Repeatable(Schedules.class)
public @interface Schedule {

    /**
     * Specifies one or more seconds with in a minute.
     *
     * @return a {@link java.lang.String} object.
     */
    String second() default "0";

    /**
     * Specifies one or more minutes with an hour.
     *
     * @return a {@link java.lang.String} object.
     */
    String minute() default "0";

    /**
     * Specifies one or more hours within a day.
     *
     * @return a {@link java.lang.String} object.
     */
    String hour() default "0";

    /**
     * Specifies one or more days within a month.
     *
     * @return a {@link java.lang.String} object.
     */
    String dayOfMonth() default "*";

    /**
     * Specifies one or more months within a year.
     *
     * @return a {@link java.lang.String} object.
     */
    String month() default "*";

    /**
     * Specifies one or more days within a week.
     *
     * @return a {@link java.lang.String} object.
     */
    String dayOfWeek() default "*";

    /**
     * Specifies one or more years.
     *
     * @return a {@link java.lang.String} object.
     */
    String year() default "*";

    /**
     * Specifies the time zone within which the schedule is evaluated. Time zones are specified as an ID string. The set of
     * required time zone IDs is defined by the Zone Name(TZ) column of the public domain zoneinfo database.
     * <p>
     * If a timezone is not specified, the schedule is evaluated in the context of the default timezone associated with the
     * contianer in which the application is executing.
     *
     * @return a {@link java.lang.String} object.
     */
    String timezone() default "";

    /**
     * Specifies an information string that is associated with the timer
     *
     * @return a {@link java.lang.String} object.
     */
    String info() default "";

    /**
     * Specifies whether the timer that is created is persistent.
     *
     * @return a boolean.
     */
    boolean persistent() default true;
}
