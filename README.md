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


