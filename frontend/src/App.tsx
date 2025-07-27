
import { useState } from "react";
import DatabaseSelector from "./components/DatabaseSelector";
import TableList from "./components/TableList";
import TableViewer from "./components/TableViewer";

function App() {
  const [selectedDb, setSelectedDb] = useState<string | null>(null);
  const [selectedTable, setSelectedTable] = useState<string | null>(null);

  return (
    <div className="p-4 max-w-screen-xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">SQLite Browser</h1>
      <DatabaseSelector onSelectDb={(db) => {
        setSelectedDb(db);
        setSelectedTable(null);
      }} />

      {selectedDb && (
        <div className="mt-4">
          <TableList
            dbName={selectedDb}
            onSelectTable={(table) => setSelectedTable(table)}
          />
        </div>
      )}

      {selectedDb && selectedTable && (
        <div className="mt-4">
          <TableViewer dbName={selectedDb} tableName={selectedTable} />
        </div>
      )}
    </div>
  );
}

export default App;