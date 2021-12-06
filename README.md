# J-T-Core for Ecore

Author: Pierre-Olivier Talbot \
Editors: Sebastien Ehouan, Théo Le Calvar, An Li

## Description

J-T-Core for Ecore is a framework for designing model transformation languages using 8 primitive modules: Matcher, Iterator, Rewriter, Resolver, Rollbacker, Selector, Synchronizer and Composer.

## Typical usage

A sample Main class is provided: `MainTest` .

**Important:** The `utils.initialize()` function must always be invoked before importing models.

Here is a typical workflow:
1. Import metamodels
2. Import models and patterns
3. Create initial packet
4. Create rules using the `RuleFactory`
5. Run packet through rules
6. Save model changes

## Ramifier

Ramifier is provided in `j-t-core/Ramifier_New` \
An Ant build file is provided in the directory, pointing to the original, LHS, RHS and augmented metamodels. \
To run the ramifier, update the paths to the models in the `build.xml` file inside the directory, then execute it.

## Unit Tests

Several Unit tests are provided in `j-t-core/src/unit/tests` .

To run them, you need:
* A metamodel
* The ramified version of the metamodel
* A dynamic instance of the metamodel
* A Precondition corresponding to the model
* NACs (Optional)
* Conditions defined to match the label IDs of the attributes, not used for VF2 in this iteration (Expected output)

The resources mentioned aboved are to be predefined in `j-t-core/Ramifier_New/Model`
