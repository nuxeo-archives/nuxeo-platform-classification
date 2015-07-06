/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.classification.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * Classification service is used to register classifiable Document Types.
 *
 * @author ldoguin
 */
public interface ClassificationService {

    enum CLASSIFY_STATE {
        CLASSIFIED, INVALID, ALREADY_CLASSIFIED
    }

    enum UNCLASSIFY_STATE {
        NOT_CLASSIFIED, UNCLASSIFIED, NOT_ENOUGH_RIGHTS
    }

    /**
     * This will return only the document types that was contributed as classifiable. It is recommanded to use the facet
     * Classifiable instead of the contribution.
     *
     * @return the list of registered Document Types as String
     */
    @Deprecated
    List<String> getClassifiableDocumentTypes();

    /**
     * If this type of document is classifiable. It is recommanded to use {@link #isClassifiable(DocumentModel)} and to
     * addResolver the facet Classifiable to the classifiable document instead of use the contribution.
     *
     * @param docType
     * @return true if the given doc type is registered.
     */
    @Deprecated
    boolean isClassifiable(String docType);

    /**
     * If this document is classifiable
     *
     * @param doc
     * @return
     */
    boolean isClassifiable(DocumentModel doc);

    /**
     * Try to classify targets document into the classificationFolder. Method return an object containing references to
     * classified documents, already classified documents or invalid documents. Lists are not initialized until there is
     * at least one corresponding documents.
     *
     * @since 5.7
     * @param classificationFolder expected classification folder
     * @param targetDocs documents wanted to be classified
     */
    ClassificationResult<CLASSIFY_STATE> classify(DocumentModel classificationFolder,
            Collection<DocumentModel> targetDocs);

    /**
     * Classify a list of documents associated them a resolver to perform complex resolution.
     *
     * @since 5.7
     */
    ClassificationResult<CLASSIFY_STATE> classify(DocumentModel classificationFolder, String resolver,
            Collection<DocumentModel> targetDocs);

    /**
     * Try to unclassify targets document into the classificationFolder. Method return an object containing references
     * to unclassified documents and not classified. Lists are not initialized until there is at least one corresponding
     * documents.
     *
     * @since 5.7
     * @param classificationFolder expected classification folder
     * @param targetDocs documents id wanted to be unclassified
     */
    ClassificationResult<UNCLASSIFY_STATE> unClassify(DocumentModel classificationFolder, Collection<String> targetDocs);

    /**
     * Try to unclassify targetId from classificationFolders. Method return an object containing references to
     * unclassified documents and not classified. Lists are not initialized until there is at least one corresponding
     * documents.
     *
     * @since 5.7
     * @param targetId whanted to be unclassified from specific container
     */
    ClassificationResult<UNCLASSIFY_STATE> unClassifyFrom(Collection<DocumentModel> classificationFolders,
            String targetId);

    /**
     * Resolve the expected document id using the resolver contributed as name. Resolution is made with an Unrestricted
     * session to prevent from document rights problem.
     *
     * @since 5.7
     */
    String resolveClassification(CoreSession session, String name, String targetDocId);

}
