/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     ldoguin
 *
 * $Id$
 */

package org.nuxeo.ecm.classification.core.test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.ALREADY_CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.INVALID;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.NOT_CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.UNCLASSIFIED;

import org.junit.runner.RunWith;
import org.nuxeo.ecm.classification.api.ClassificationResult;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.platform.classification.api", "org.nuxeo.ecm.platform.classification.core" })
@LocalDeploy({ "org.nuxeo.ecm.platform.classification.core:OSGI-INF/classification-classifiable-types-test-contrib.xml" })
public class TestClassificationService {

    @Inject
    protected ClassificationService cs;

    @Inject
    protected CoreSession session;

    @Before
    public void setUp() throws Exception {
        assertNotNull(cs);
    }

    @Test
    public void testRegistration() {
        assertNotNull(cs);
        assertNotNull(cs.getClassifiableDocumentTypes());
        assertTrue(cs.isClassifiable("File"));
        assertTrue(cs.isClassifiable("Note"));
        assertTrue(cs.isClassifiable("Folder"));
        assertEquals(6, cs.getClassifiableDocumentTypes().size());
    }

    @Test
    public void testClassifiable() {
        DocumentModel folder = session.createDocumentModel("/", "foo", "Folder");
        folder = session.createDocument(folder);
        assertTrue(cs.isClassifiable(folder));
        DocumentModel classifiableDoc = session.createDocumentModel("/", "bar", "ClassifiableDoc");
        assertTrue(cs.isClassifiable(classifiableDoc));
    }

    @Test
    public void testClassify() {
        DocumentModel classifFolder = session.createDocument(session.createDocumentModel("/default-domain", "cFolder",
                "ClassificationFolder"));

        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        final String testWorkspace = "/default-domain/workspaces/test";

        for (int i = 0; i < 4; i++) {
            docs.add(session.createDocument(session.createDocumentModel(testWorkspace, "xxx", "File")));
        }

        session.save();

        assertEquals(4, docs.size());
        assertEquals(0, session.getChildren(classifFolder.getRef()).size());

        ClassificationResult<ClassificationService.CLASSIFY_STATE> classified = cs.classify(classifFolder, docs);
        assertEquals(4, classified.get(CLASSIFIED).size());
        assertFalse(classified.contains(INVALID));
        assertFalse(classified.contains(ALREADY_CLASSIFIED));

        session.save();
        classifFolder = session.getDocument(classifFolder.getRef());
        assertEquals(4, classifFolder.getAdapter(Classification.class).getClassifiedDocumentIds().size());

        docs.add(session.createDocument(session.createDocumentModel(testWorkspace, "xxx-", "File")));
        classified = cs.classify(classifFolder, docs);
        assertEquals(1, classified.get(CLASSIFIED).size());
        assertEquals(4, classified.get(ALREADY_CLASSIFIED).size());

        List<String> docIds = new ArrayList<String>();
        docIds.add(session.createDocument(session.createDocumentModel(testWorkspace, "xxx-", "File")).getId());
        docIds.add(session.getChild(new PathRef(testWorkspace), "xxx").getId());

        session.save();
        classifFolder = session.getDocument(classifFolder.getRef());
        assertEquals(5, classifFolder.getAdapter(Classification.class).getClassifiedDocumentIds().size());

        ClassificationResult<ClassificationService.UNCLASSIFY_STATE> unclassified = cs.unClassify(classifFolder, docIds);
        assertEquals(1, unclassified.get(NOT_CLASSIFIED).size());
        assertEquals(1, unclassified.get(UNCLASSIFIED).size());
    }
}
