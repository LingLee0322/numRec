/****************************************************************************
 *
 * $Id: Enum.java 15643 2012-02-10 08:12:40Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.util */
import java.util.List;

/**
 * Base class for typesafe enumerations. Typesafe enumeration classes
 * provide better compile time and runtime checking than other alternatives.
 * The basic idea is simple: define a class representing a single element
 * of the enumerated type, and don't provide any public constructors for it.
 * Just provide public static final fields, one for each constant in the
 * enumerated type. Because there is no way for clients to create objects
 * of the type, there will never be any objects of the type besides those
 * exported via the public static final fields.
 */
class Enum {

    /**
     * Description string
     */
    private String description;

    /**
     * Default constructor is disabled.
     */
    private Enum() {
        throw new InternalError("Default constructor is disabled");
    }

    /**
     * Creates a new enum value.
     * @param description description string
     */
    protected Enum(String description) {
        if (description != null) {
            this.description = description;
        } else {
            throw new NullPointerException("description");
        }
    }

    /**
     * Returns the description of this object.
     * @return The description of this object.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns the hashcode for this object.
     * @return The hashcode for this object.
     */
    public int hashCode() {
        return description.hashCode();
    }

    /**
     * Returns a string representation of this object. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  A string representation of this object.
     */
    public final String toString() {
        return description;
    }

    /**
     * Enum with an integer value associated with it.
     * @see Enum
     */
    static class Int extends Enum implements Comparable {

        /**
         * Integer value that identifies this enum value
         */
        private int value;

        /**
         * Creates a new enum value.
         * @param value integer value that identifies this enum value
         * @param description description string
         */
        protected Int(int value, String description) {
            super(description);
            this.value = value;
        }

        /**
         * Gets the integer value associated with this object.
         * @return The integer value that identifies this object
         */
        public final int getValue() {
            return value;
        }

        /**
         * Returns the hashcode for this object.
         * @return The hashcode for this object.
         */
        public final int hashCode() {
            return value;
        }

        /**
         * Compares this object with the specified object for order.
         * Returns a negative integer, zero, or a positive integer as
         * this object is less than, equal to, or greater than the
         * specified object.
         *
         * @param   obj the Object to be compared.
         * @return  a negative integer, zero, or a positive integer as
         *          this object is less than, equal to, or greater than
         *          the specified object.
         *
         * @throws ClassCastException if the specified object's type prevents it
         *         from being compared to this Object.
         *
         * @see Comparable
         */
        public int compareTo(Object obj) {
            int thisVal = this.value;
            int thatVal = ((Int)obj).value;
            return (thisVal < thatVal ? -1 : (thisVal == thatVal ? 0 : 1));
        }

        /**
         * Compares id of this object with the specified key for order.
         * Returns a negative integer, zero, or a positive integer as id
         * of this object is less than, equal to, or greater than the
         * specified key.
         *
         * @param   key the key to be compared.
         * @return  a negative integer, zero, or a positive integer as id
         *          of this object is less than, equal to, or greater than
         *          the specified key.
         *
         * @throws ClassCastException if the specified object's type prevents it
         *         from being compared to this Object.
         *
         * @see Comparable
         */
        public int compareTo(int key) {
            int thisVal = this.value;
            return (thisVal < key ? -1 : (thisVal == key ? 0 : 1));
        }

        /**
         * Searches the list for the specified enum object using the binary
         * search algorithm. The list must be sorted.
         * @param list the list to search
         * @param key the key to search for
         * @return index of the enum object, if it is contained in the
         *   list; otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.
         */
        static protected int search(List list, int key) {
            int low = 0;
            int high = list.size()-1;

            while (low <= high) {
                int mid =(low + high)/2;
                Int type = (Int)list.get(mid);
                int cmp = type.compareTo(key);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid; // key found
                }
            }

            return -(low + 1);  // key not found
         }
    }
}
