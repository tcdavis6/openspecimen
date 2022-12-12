export default {
  layout: {
    rows: [
      {
        fields: [
          {
            name: "criteria.freezer",
            labelCode: "containers.freezer",
            type: "multiselect",
            listSource: {
              apiUrl: "storage-containers",
              searchProp: "name",
              displayProp: "name",
              selectProp: "name",
              valueProp: "naam",
              queryParams: {
                static: {
                  topLevelContainers: true,
                  status: ['AVAILABLE', 'CHECKED_OUT']
                }
              }
            }
          }
        ]
      },
      {
        fields: [
          {
            name: "criteria.cp",
            labelCode: "containers.cps",
            type: "multiselect",
            listSource: {
              apiUrl: "collection-protocols",
              displayProp: "shortTitle",
              selectProp: "shortTitle",
            }
          }
        ]
      },
      {
        fields: [
          {
            name: "criteria.fromDate",
            labelCode: "containers.from_date",
            type: "datePicker"
          }
        ]
      },
      {
        fields: [
          {
            name: "criteria.toDate",
            labelCode: "containers.to_date",
            type: "datePicker"
          }
        ]
      }
    ]
  }
}
