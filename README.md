# Nuxeo Classification / Shared Bookmark README

Nuxeo shared bookmark is an addon for Nuxeo EP.
It provides two new Document Types, ClassificationRoot and
ClassificationFolder, which let you bookmark documents in it.

You can create arborescence of folder in which to fill documents.
When a document is filled, a symlink to this document is created
in the classification folder (we do not copy/snapshot the document)

## Deploying

Install [the Nuxeo Shared Bookmarks Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-shared-bookmarks).
Or manually copy the built artifacts into `$NUXEO_HOME/templates/custom/bundles/` and activate the "custom" template.

## Usage for a custom project

### Make a document Bookmarkable

A document is bookmarkable if you have registered a contribution to the
ClassificationService as follow:

    <extension target="org.nuxeo.ecm.classification.core.ClassificationService"
        point="types">
      <classifiable type="File" />
    </extension>

The classify button will then be enabled for the File Documents.

### Create a Bookmarkable Document Type

To create a new Document Type with bookmarks features, just add the
Classifiable facet to its core definition.

    <doctype name="MySampleDoc" extends="File">
      <facet name="Classifiable" />
    </doctype>

### Use resolver to alter document link resolution

When you want to make a dynamique link, for instance when you want to bookmark always the last version of a document, you can use ClassificationResolver. Method org.nuxeo.ecm.classification.core.ClassificationServiceImpl#classify allow you to do that.

You can contribute a new resolver like this:

    <extension target="org.nuxeo.ecm.classification.core.ClassificationService"
             point="resolvers">
      <resolver name="lastVersion" class="org.nuxeo.ecm.classification.core.resolver.LastVersionResolver" />
    </extension>

## Bundle description

  .
  ├── nuxeo-classification-api: API java sources and Classification Adapter.
  ├── nuxeo-classification-core: Coew java sources, with service implementation and resolvers.
  ├── nuxeo-classification-web: Web ressources with Seam beans, tree and templates files.
  └── nuxeo-classification-marketplace: Marketplace and functional test module
      ├── ftest/selenium: Selenium test cases / suite
      └── marketplace-explicit: Module building marketplace package file

## TODO

- fix hack in
  ClassificationActionsBean.cancelCurrentSelectionClassificationForm

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-platform-classification-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-platform-classification-master/)

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
