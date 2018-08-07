package Test;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class GrowingFlower {

  /** Informal test harness. */
  public static void main(String... arguments) {
    GrowingFlower tulip = new GrowingFlower("Tulip", 1);
    tulip.grow();
    tulip.grow();
    log(tulip);

    tulip.randomGrowOrWither();
    log(tulip);

    tulip.wither();
    tulip.wither();
    log(tulip);
  }

  /**
   Constructor.
   @param species must have content.
   @param initialLength must be greater than 0.
  */
  public GrowingFlower(String species, int initialLength) {
    //assert is NOT used to validate params of public methods
    if (!isValidSpecies(species)) {
      throw new IllegalArgumentException("Species must have content.");
    }
    if (!isValidLength(initialLength)) {
      throw new IllegalArgumentException("Initial length must be positive.");
    }

    this.species = species;
    this.length = initialLength;

    //check the class invariant
    assert hasValidState(): "Construction failed - not valid state.";
  }

  public boolean isMature() {
    return length > 5 ;
    //not necessary to assert valid state here, since
    //the state has not changed.
  }

  /**
   Increase the length by at least one unit.
  */
  public void grow(){
    //this style of checking post-conditions is NOT recommended,
    //since the copy of length is always made, even when
    //assertions are disabled.
    //See <tt>wither</tt> (below) for an example with an improved style.
    int oldLength = length;
    length += getLengthIncrease(length);
    //post-condition: length has increased
    assert length > oldLength;

    //check the class invariant
    assert hasValidState(): this;
  }

  /**
   Decrease the length by one unit, but only if the resulting length
   will still be greater than 0.
  */
  public void wither(){

    //this local class exists only to take a snapshot of the current state.
    //although bulky, this style allows post-conditions of arbitrary complexity.
    class OriginalState {
      OriginalState() {
        originalLength = length;
      }
      int getLength() {
        return originalLength;
      }
      private final int originalLength;
    }
    OriginalState originalState = null;
    //construct an object inside an assertion, in order to ensure that
    //no construction takes place when assertions are disabled.
    //this assert is rather unusual in that it will always succeed, and in that
    //it has side-effects - it creates an object and sets a reference
    assert (originalState = new OriginalState()) != null;

    if (length > 1) {
      --length;
    }

    //post-condition: length has decreased by one or has remained the same
    assert length <= originalState.getLength();

    //check the class invariant
    assert hasValidState(): this;
  }

  /**
   Randomly select one of three actions.
   The actions are:
   <ul>
    <li>do nothing
    <li>grow
    <li>wither
   </ul>
  */
  public void randomGrowOrWither() {
    //(magic numbers are used here instead of symbolic constants
    //to slightly clarify the example)
    ThreadLocalRandom generator = ThreadLocalRandom.current();
    int action = generator.nextInt(3);
    //action will take one of the values 0,1,2.
    if (action == 0) {
      //do nothing
    }
    else if (action == 1) {
      grow();
    }
    else if (action == 2) {
      wither();
    }
    else {
      //this is still executed if assertions are disabled
      throw new AssertionError("Unexpected value for action: " + action);
    }
    //check the class invariant
    assert hasValidState(): this;
  }

  /** Use for debugging only.  */
  @Override public String toString(){
    StringBuilder result = new StringBuilder();
    result.append(this.getClass().getName());
    result.append(": Species=");
    result.append(species);
    result.append(" Length=");
    result.append(length);
    return result.toString();
  }
  
  //equals and hashCode are elided

  // PRIVATE 
  private String species;
  private int length;
  
  private static void log(Object msg){
    System.out.println(Objects.toString(msg));
  }

  /**
   Implements the class invariant.
  
   Perform all checks on the state of the object.
   One may assert that this method returns true at the end
   of every public method.
  */
  private boolean hasValidState(){
    return isValidSpecies(species) && isValidLength(length);
  }

  /** Species must have content.  */
  private boolean isValidSpecies(String species) {
    return species != null && species.trim().length()>0;
  }

  /** Length must be greater than 0.  */
  private boolean isValidLength(int length) {
    return length > 0;
  }

  /** Length increase depends on the current length. */
  private int getLengthIncrease(int originalLength) {
    //since this is a private method, an assertion
    //may be used to validate the argument
    assert originalLength > 0: this;
    int result = originalLength > 10 ? 2 : 1;
    assert result > 0 : result;
    return result;
  }
}