import http from '@/common/services/HttpClient.js';

export default {
  layout: {
    rows: [
      {
        fields: [
          {
            type: "fileUpload",
            name: "job.inputFileId",
            labelCode: "import.input_records_file",
            url: () => http.getUrl("import-jobs/input-file"),
            headers: () => http.headers,
            validations: {
              required: {
                messageCode: "import.input_records_file_req"
              }
            }
          }
        ]
      },
      {
        fields: [
          {
            type: "radio",
            name: "job.importType",
            labelCode: "import.import_type",
            options: (context) => context.formData.getImportTypes(),
            optionsPerRow: 3,
            validations: {
              required: {
                messageCode: "import.import_type_req"
              }
            }
          }
        ]
      },
      {
        labelCode: "import.date_n_time_format",
        fields: [
          {
            type: "pv",
            attribute: "date_format",
            selectProp: "value",
            name: "job.dateFormat",
            labelCode: "import.date",
            validations: {
              required: {
                messageCode: "import.date_format_required"
              }
            }
          },
          {
            type: "pv",
            attribute: "time_format",
            selectProp: "value",
            name: "job.timeFormat",
            labelCode: "import.time",
            validations: {
              required: {
                messageCode: "import.time_format_required"
              }
            }
          }
        ]
      },
      {
        fields: [
          {
            type: "text",
            name: "job.fieldSeparator",
            labelCode: "import.field_separator"
          }
        ]
      }
    ]
  }
}
