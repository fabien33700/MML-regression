/**
 * generated by Xtext 2.19.0
 */
package org.xtext.example.mydsl.mml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Framework Lang</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.xtext.example.mydsl.mml.MmlPackage#getFrameworkLang()
 * @model
 * @generated
 */
public enum FrameworkLang implements Enumerator
{
  /**
   * The '<em><b>SCIKIT</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #SCIKIT_VALUE
   * @generated
   * @ordered
   */
  SCIKIT(0, "SCIKIT", "scikit-learn"),

  /**
   * The '<em><b>R</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #R_VALUE
   * @generated
   * @ordered
   */
  R(1, "R", "R"),

  /**
   * The '<em><b>Java Weka</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JAVA_WEKA_VALUE
   * @generated
   * @ordered
   */
  JAVA_WEKA(2, "JavaWeka", "Weka"),

  /**
   * The '<em><b>XG Boost</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #XG_BOOST_VALUE
   * @generated
   * @ordered
   */
  XG_BOOST(3, "XGBoost", "xgboost"),

  /**
   * The '<em><b>Tensor Flow</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TENSOR_FLOW_VALUE
   * @generated
   * @ordered
   */
  TENSOR_FLOW(4, "TensorFlow", "TensorFlow");

  /**
   * The '<em><b>SCIKIT</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #SCIKIT
   * @model literal="scikit-learn"
   * @generated
   * @ordered
   */
  public static final int SCIKIT_VALUE = 0;

  /**
   * The '<em><b>R</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #R
   * @model
   * @generated
   * @ordered
   */
  public static final int R_VALUE = 1;

  /**
   * The '<em><b>Java Weka</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #JAVA_WEKA
   * @model name="JavaWeka" literal="Weka"
   * @generated
   * @ordered
   */
  public static final int JAVA_WEKA_VALUE = 2;

  /**
   * The '<em><b>XG Boost</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #XG_BOOST
   * @model name="XGBoost" literal="xgboost"
   * @generated
   * @ordered
   */
  public static final int XG_BOOST_VALUE = 3;

  /**
   * The '<em><b>Tensor Flow</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #TENSOR_FLOW
   * @model name="TensorFlow"
   * @generated
   * @ordered
   */
  public static final int TENSOR_FLOW_VALUE = 4;

  /**
   * An array of all the '<em><b>Framework Lang</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final FrameworkLang[] VALUES_ARRAY =
    new FrameworkLang[]
    {
      SCIKIT,
      R,
      JAVA_WEKA,
      XG_BOOST,
      TENSOR_FLOW,
    };

  /**
   * A public read-only list of all the '<em><b>Framework Lang</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<FrameworkLang> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Framework Lang</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static FrameworkLang get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      FrameworkLang result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Framework Lang</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static FrameworkLang getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      FrameworkLang result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Framework Lang</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static FrameworkLang get(int value)
  {
    switch (value)
    {
      case SCIKIT_VALUE: return SCIKIT;
      case R_VALUE: return R;
      case JAVA_WEKA_VALUE: return JAVA_WEKA;
      case XG_BOOST_VALUE: return XG_BOOST;
      case TENSOR_FLOW_VALUE: return TENSOR_FLOW;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private FrameworkLang(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }
  
} //FrameworkLang
