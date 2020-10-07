# J-T-Core for Ecore
Author: Pierre-Olivier Talbot

## Description
J-T-Core for Ecore is a framework for designing model transformation languages
using 8 primitive modules: Matcher, Iterator, Rewriter, Resolver, Rollbacker, Selector, Synchronizer and Composer.

## Typical usage
A sample Main class is provided: `MainTest`.

**Important:** The `utils.initialize()` function must always be invoked before importing models.

Here is a typical workflow:
1. Import metamodels
2. Import models and patterns
3. Create initial packet
4. Create rules using the `RuleFactory`
5. Run packet through rules
6. Save model changes

## Unit Tests
Several Unit tests are provided in `j-t-core/src/unit/tests`.

To run them, you need:
- A metamodel
- The ramified version of the metamodel
- A dynamic instance of the metamodel
- A Precondition corresponding to the model
- NACs (Optional)
- Conditions defined to match the label IDs of the attributes. (Expected output)

The resources mentioned aboved are to be predefined in `j-t-core/Ramifier_New/Model`
