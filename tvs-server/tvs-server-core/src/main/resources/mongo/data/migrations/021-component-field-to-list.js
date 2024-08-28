function transformComponent() {
    db.testCaseResult.find({"COMPONENT": {$exists: true}, "COMPONENTS": {$exists: false}})
        .snapshot().forEach(function (testCase) {
            testCase.COMPONENTS = [testCase.COMPONENT];
            delete testCase.COMPONENT;
            db.testCaseResult.save(testCase);
        }
    );
}
