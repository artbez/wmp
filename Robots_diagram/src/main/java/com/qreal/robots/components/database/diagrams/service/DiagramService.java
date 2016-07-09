package com.qreal.robots.components.database.diagrams.service;

import com.qreal.robots.components.editor.model.diagram.Diagram;
import com.qreal.robots.components.editor.model.diagram.DiagramRequest;
import com.qreal.robots.components.editor.model.diagram.Folder;

//In service transactions are added to DAO. DAO only DB functions implements.

public interface DiagramService {

    public Long saveDiagram(DiagramRequest diagramRequest);

    public Diagram openDiagram(Long diagramId);

    public void rewriteDiagram(Diagram diagram);

    public void deleteDiagram(Long diagramId);

    public void createRootFolder(String userName);

    public Long createFolder(Folder folder);

    public void deleteFolder(Long folderId);

    public Folder getFolderTree();

}