/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2008-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.types;

import static org.opends.server.core.DirectoryServer.*;
import static org.opends.server.util.CollectionUtils.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.schema.AttributeType;
import org.forgerock.opendj.ldap.schema.Schema;
import org.opends.server.types.Attribute.RemoveOnceSwitchingAttributes;

/**
 * This class contains various methods for manipulating
 * {@link Attribute}s as well as static factory methods for
 * facilitating common {@link Attribute} construction use-cases.
 * <p>
 * Of particular interest are the following three factory methods:
 *
 * <pre>
 * empty(String);
 *
 * create(String, String);
 *
 * create(String, String, String...);
 * </pre>
 *
 * These are provided in order to facilitate construction of empty,
 * single-valued, and multi-valued attributes respectively, for
 * example, in unit tests. The last factory method is not designed for
 * performance critical functionality and, instead, an
 * {@link AttributeBuilder} should be used in order to incrementally
 * construct multi-valued attributes.
 */
@RemoveOnceSwitchingAttributes
public final class Attributes
{

  /**
   * Creates a new single-valued attribute with the specified attribute type and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param value
   *          The attribute value.
   * @return A new attribute with the attribute type and value.
   */
  public static Attribute create(AttributeType attributeType, ByteString value)
  {
    return create(attributeType, attributeType.getNameOrOID(), value);
  }

  /**
   * Creates a new List with a single-valued attribute with the specified attribute type and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param value
   *          The attribute value.
   * @return A new List with a single-valued attribute with the attribute type and value.
   */
  public static List<Attribute> createAsList(AttributeType attributeType, ByteString value)
  {
    return newArrayList(create(attributeType, value));
  }

  /**
   * Creates a new single-valued attribute with the specified name and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param valueString
   *          The String representation of the attribute value.
   * @return A new attribute with the specified name and value.
   */
  public static Attribute create(AttributeType attributeType, String valueString)
  {
    return create(attributeType, attributeType.getNameOrOID(), valueString);
  }

  /**
   * Creates a new List with a single-valued attribute with the specified name and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param valueString
   *          The String representation of the attribute value.
   * @return A new List with a attribute with the specified name and value.
   */
  public static List<Attribute> createAsList(AttributeType attributeType, String valueString)
  {
    return newArrayList(create(attributeType, valueString));
  }

  /**
   * Creates a new single-valued attribute with the specified
   * attribute type and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param name
   *          The user-provided name for this attribute.
   * @param value
   *          The attribute value.
   * @return A new attribute with the attribute type and value.
   */
  public static Attribute create(AttributeType attributeType, String name, ByteString value)
  {
    return AttributeBuilder.create(attributeType, name, Collections.singleton(value));
  }

  /**
   * Creates a new single-valued attribute with the attribute type and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param name
   *          The user-provided name for this attribute.
   * @param valueString
   *          The String representation of the attribute value.
   * @return A new attribute with the attribute type and value.
   */
  public static Attribute create(AttributeType attributeType, String name, String valueString)
  {
    return create(attributeType, name, ByteString.valueOfUtf8(valueString));
  }

  /**
   * Creates a new List with a single-valued attribute with the attribute type and value.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param name
   *          The user-provided name for this attribute.
   * @param valueString
   *          The String representation of the attribute value.
   * @return A new List with a single-valued attribute with the attribute type and value.
   */

  public static List<Attribute> createAsList(AttributeType attributeType, String name, String valueString)
  {
    return newArrayList(create(attributeType, name, valueString));
  }

  /**
   * Creates a new single-valued attribute with the specified
   * attribute name and attribute value.
   * <p>
   * If the attribute name cannot be found in the schema, a new
   * attribute type is created using the default attribute syntax.
   *
   * @param attributeName
   *          The name or OID of the attribute type for this attribute
   *          (can be mixed case).
   * @param valueString
   *          The String representation of the attribute value.
   * @return A new attribute with the specified name and value.
   */
  public static Attribute create(String attributeName, String valueString)
  {
    return create(getSchema().getAttributeType(attributeName), attributeName, valueString);
  }

  /**
   * Creates a new multi-valued attribute with the specified attribute
   * name and attribute values.
   * <p>
   * If the attribute name cannot be found in the schema, a new
   * attribute type is created using the default attribute syntax.
   * <p>
   * <b>NOTE:</b> this method is provided as a convenience and should
   * typically be reserved for use in unit tests and places where
   * performance is not an issue. In particular, this method will
   * construct a temporary array containing the attribute's values.
   * For performance critical purposes, incrementally construct an
   * attribute using an {@link AttributeBuilder}.
   *
   * @param attributeName
   *          The name or OID of the attribute type for this attribute
   *          (can be mixed case).
   * @param valueStrings
   *          The string representation of the attribute values.
   * @return A new attribute with the specified name and values.
   */
  public static Attribute create(String attributeName, String... valueStrings)
  {
    if (valueStrings.length == 0) {
      return empty(attributeName);
    }
    AttributeBuilder builder = new AttributeBuilder(attributeName);
    builder.addAllStrings(Arrays.asList(valueStrings));
    return builder.toAttribute();
  }

  /**
   * Creates a new attribute which has the same attribute type and
   * attribute options as the provided attribute but no attribute
   * values.
   *
   * @param attribute
   *          The attribute to be copied.
   * @return A new attribute which has the same attribute type and
   *         attribute options as the provided attribute but no
   *         attribute values.
   */
  public static Attribute empty(Attribute attribute)
  {
    return new AttributeBuilder(attribute.getAttributeDescription()).toAttribute();
  }



  /**
   * Creates a new attribute with the provided attribute type and no
   * values.
   *
   * @param attributeType
   *          The attribute type to use.
   * @return A new attribute with the provided attribute type and no
   *         values.
   */
  public static Attribute empty(AttributeType attributeType)
  {
    return empty(attributeType, attributeType.getNameOrOID());
  }



  /**
   * Creates a new attribute with the provided attribute type and no
   * values.
   *
   * @param attributeType
   *          The attribute type to use.
   * @param name
   *          The user-provided name for this attribute.
   * @return A new attribute with the provided attribute type and no
   *         values.
   */
  public static Attribute empty(AttributeType attributeType,
      String name)
  {
    return AttributeBuilder.create(attributeType, name, Collections
        .<ByteString> emptySet());
  }



  /**
   * Creates a new attribute with the specified attribute name and no
   * attribute values.
   * <p>
   * If the attribute name cannot be found in the schema, a new
   * attribute type is created using the default attribute syntax.
   *
   * @param attributeName
   *          The name or OID of the attribute type for this attribute
   *          (can be mixed case).
   * @return A new attribute with the specified name and no values.
   */
  public static Attribute empty(String attributeName)
  {
    return empty(getSchema().getAttributeType(attributeName), attributeName);
  }

  private static Schema getSchema()
  {
    return getInstance().getServerContext().getSchema();
  }



  /**
   * Creates a new attribute containing all the values from the two
   * provided attributes. The returned attribute will use the name and
   * options taken from the first attribute.
   * <p>
   * This method is logically equivalent to:
   *
   * <pre>
   * merge(a1, a2, null);
   * </pre>
   *
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute.
   * @return A new attribute containing all the values from the two
   *         provided attributes.
   */
  public static Attribute merge(Attribute a1, Attribute a2)
  {
    return merge(a1, a2, null);
  }



  /**
   * Creates a new attribute containing all the values from the two
   * provided attributes and put any duplicate values into the
   * provided collection. The returned attribute will use the name
   * and options taken from the first attribute.
   *
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute.
   * @param duplicateValues
   *          A collection which will be used to store any duplicate
   *          values, or <code>null</code> if duplicate values should
   *          not be stored.
   * @return A new attribute containing all the values from the two
   *         provided attributes.
   */
  public static Attribute merge(Attribute a1, Attribute a2,
      Collection<ByteString> duplicateValues)
  {
    AttributeBuilder builder = new AttributeBuilder(a1);
    for (ByteString av : a2)
    {
      if (!builder.add(av) && duplicateValues != null)
      {
        duplicateValues.add(av);
      }
    }
    return builder.toAttribute();
  }



  /**
   * Creates a new attribute containing the values from the first
   * attribute which are not in the second attribute. The returned
   * attribute will use the name and options taken from the first
   * attribute.
   * <p>
   * This method is logically equivalent to:
   *
   * <pre>
   * subtract(a1, a2, null);
   * </pre>
   *
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute.
   * @return A new attribute containing the values from the first
   *         attribute which are not in the second attribute.
   */
  public static Attribute subtract(Attribute a1, Attribute a2)
  {
    return subtract(a1, a2, null);
  }



  /**
   * Creates a new attribute containing the values from the first
   * attribute which are not in the second attribute. Any values which
   * were present in the second attribute but which were not present
   * in the first attribute will be put into the provided collection.
   * The returned attribute will use the name and options taken from
   * the first attribute.
   *
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute.
   * @param missingValues
   *          A collection which will be used to store any missing
   *          values, or <code>null</code> if missing values should
   *          not be stored.
   * @return A new attribute containing the values from the first
   *         attribute which are not in the second attribute.
   */
  public static Attribute subtract(Attribute a1, Attribute a2,
      Collection<ByteString> missingValues)
  {
    AttributeBuilder builder = new AttributeBuilder(a1);
    for (ByteString av : a2)
    {
      if (!builder.remove(av) && missingValues != null)
      {
        missingValues.add(av);
      }
    }
    return builder.toAttribute();
  }
}
