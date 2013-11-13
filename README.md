jackson-module-unitsofmeasure
=============================

Contains custom serializers and deserializers for org.unitsofmeasure classes.

This was built with a custom `5.0-opower` version of JScience, which is provided in a local Maven repository, located in the
`/repo` sub-directory of this artifact. It was built from JScience version `r65` (from 2011-10-27), because as of 2013-11-05
there is no publicly released version 5.0 (or greater). It contains fixes to the OSGI and Javolution dependencies, whose Maven
coordinates have changed since the last official JScience release, and corrections to import statements for the
`org.jscience.physics.unit.system.*` classes. The source code for this version of JScience is also available in the `/repo`
sub-directory.
