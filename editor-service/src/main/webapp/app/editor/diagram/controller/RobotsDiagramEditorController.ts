/// <reference path="DiagramMenuController.ts" />
/// <reference path="parsers/DiagramThriftParser.ts" />
/// <reference path="../../interfaces/plugins/interpreter.d.ts" />
/// <reference path="../../interfaces/pluginController.d.ts" />
/// <reference path="../../interfaces/diagramFull.d.ts" />
/// <reference path="../../interfaces/vendor.d.ts" />

class RobotsDiagramEditorController extends DiagramEditorController {

    private diagramParser: DiagramThriftParser;
    private menuController: DiagramMenuController;
    private diagramInterpreter: Interpreter;

    constructor($scope, $attrs) {
        super($scope, $attrs);
        this.diagramParser = new DiagramThriftParser();
        this.menuController = new DiagramMenuController(this);
        this.diagramInterpreter = PluginController.create("Interpreter");

        $scope.openTwoDModel = () => { this.openTwoDModel(); };
        $scope.createNewDiagram = () => { this.menuController.createNewDiagram(); };
        $scope.openFolderWindow = () => { this.menuController.openFolderWindow(); };
        $scope.saveCurrentDiagram = () => { this.menuController.saveCurrentDiagram(); };
        $scope.saveDiagramAs = () => { this.menuController.saveDiagramAs(); };
        $scope.clearAll = () => { this.clearAll(); };

        $scope.$on("interpret", (event, timeline) => {
            PluginController.exec(this.diagramInterpreter, "interpret", this.getGraph(), this.getNodesMap(), this.getLinksMap(), timeline);
        });

        $scope.$on("stop", (event) => {
            PluginController.exec(this.diagramInterpreter, "stop");
        });

        this.elementsTypeLoader.load((elementTypes: ElementTypes): void => {
            this.handleLoadedTypes(elementTypes);
        });
    }

    public handleLoadedTypes(elementTypes: ElementTypes): void {
        this.propertyEditorController = new PropertyEditorController(this.sceneController, this.undoRedoController);

        for (var typeName in elementTypes.uncategorisedTypes) {
            this.nodeTypesMap[typeName] = elementTypes.uncategorisedTypes[typeName];
        }

        var categories: Map<Map<NodeType>> = elementTypes.paletteTypes.categories;
        for (var category in categories) {
            for (var typeName in categories[category]) {
                this.nodeTypesMap[typeName] = categories[category][typeName];
            }
        }

        this.paletteController.appendBlocksPalette(elementTypes.paletteTypes);
        this.paletteController.initDraggable();
    }

    public handleLoadedDiagramJson(diagram: TDiagram): void {
        var diagramParts: DiagramParts = this.diagramParser.parse(diagram, this.nodeTypesMap);
        var scene = this.diagramEditor.getScene();
        scene.addNodesFromMap(diagramParts.nodesMap);
        scene.addLinksFromMap(diagramParts.linksMap);
    }

    public getDiagramParts(): DiagramParts {
        return new DiagramParts(this.getNodesMap(), this.getLinksMap());
    }

    public openTwoDModel(): void {
        $("#diagram-area").hide();
        $("#two-d-model-area").show();
    }

    public clearAll(): void {
        this.clearState();
        this.menuController.clearState();
    }

}