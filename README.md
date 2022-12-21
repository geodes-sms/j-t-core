# J-T-Core for Ecore

Author: Pierre-Olivier Talbot \
Editors: Sebastien Ehouan, Théo Le Calvar, An Li, Maxime Boissonnat

## Description

J-T-Core for Ecore is a framework for designing model transformation languages using 8 primitive modules: Matcher, Iterator, Rewriter, Resolver, Rollbacker, Selector, Synchronizer and Composer.

## Typical usage

A sample Main class is provided: `MainTest`.

**Important:** The `utils.initialize()` function must always be invoked before importing models. \
This project requires Java 11 and add all libraries in the `lib/` directory in the classpath.

Here is a typical workflow:
1. Import metamodels
2. Import models and patterns
3. Create initial packet
4. Create rules using the `RuleFactory`
5. Run packet through rules
6. Save model changes

## Ramifier

Ramifier is provided in `j-t-core/Ramifier_New` \
To run the ramifier, please run the following ATL transformations in the below order, with the paths to the models specified:
1. `j-t-core/Ramifier_New/Transformation/Relax.atl`
2. `j-t-core/Ramifier_New/Transformation/Augment_pre.atl`
3. `j-t-core/Ramifier_New/Transformation/Augment_post.atl`
4. `j-t-core/Ramifier_New/Transformation/Augment_compo.atl`

Note: To be able to run the transformations, when creating the configuration, go to the Advanced tab, select "EMF-specific VM", and check "Allow inter-model references" under "Advanced parameters".

Please, make sure that your .project file conforms to the following :
```
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>j-t-core</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.m2m.atl.adt.builder.atlBuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.m2m.atl.adt.builder.atlNature</nature>
	</natures>
</projectDescription>
```
## Unit Tests

Several Unit tests are provided in `j-t-core/src/unit/tests`.

To run them, you need:
* A metamodel (yourmetamodel.ecore)
* The ramified version of the metamodel (by applying the ATL transformations in the order stated above)
* A dynamic instance of the metamodel (from the metamodel create a new dynamic instance of your base class)
* A Precondition corresponding to the model, it is a new dynamic instance created from the augmented metamodel's (yourmetamodel_augmented.ecore) base class (ex : MTpre__A -> MTpre__Element)
* NACs (Optional)
* Conditions defined to match the label IDs of the attributes, not used for VF2 in this iteration (Expected output)

⚠️ **Please, make sure that your Pattern objects' `MT label` attribute matches your Model objects' `Id` attribute** ⚠️

The resources mentioned above are to be predefined in `j-t-core/Ramifier_New/Model`

## Video example

For a better understanding, here's a complete of how a Unit Test should be created :


https://user-images.githubusercontent.com/80822665/209014464-e956e2ac-d28c-44a3-a8a0-a8847f363d31.mp4

