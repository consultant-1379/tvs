export default class StatusService {

    constructor(resultCodeResource) {
        'ngInject';
        this.resultCodeResource = resultCodeResource;
        this.statuses = [];
    }

    load() {
        this.resultCodeResource.get().$promise
            .then(st => {
                this.statuses = st.statuses;
            });
    }

    getStatuses() {
        return this.statuses;
    }

}
