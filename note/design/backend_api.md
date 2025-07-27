
| method | path                                         | description                                    |
| ------ | -------------------------------------------- | ---------------------------------------------- |
| `GET`  | `/api/databases`                             | list databases                                 |
| `GET`  | `/api/databases/{db}/tables`                 | list tables in database                        |
| `GET`  | `/api/databases/{db}/tables/{table}`         | get table data（support limit/offset）          |
| `GET`  | `/api/databases/{db}/tables/{table}/columns` | list struct, metadata（by `PRAGMA table_info`） |

