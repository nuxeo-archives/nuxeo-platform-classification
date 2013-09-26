# Nuxeo Classification / Shared Bookmark README

Nuxeo shared bookmark is an addon for Nuxeo EP.
It provides two new Document Types, ClassificationRoot and
ClassificationFolder, which let you bookmark documents in it.

You can create arborescence of folder in which to fill documents.
When a document is filled, a symlink to this document is created
in the classification folder (we do not copy/snapshot the document)

## Install

To install classification addon on nuxeo, build and copy all packages
in nxserver/plugins.

Or install the dedicated [Nuxeo Marketplace](http://marketplace.nuxeo.com/) package using the [Admin Center](http://doc.nuxeo.com/x/lYFH)

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

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>
