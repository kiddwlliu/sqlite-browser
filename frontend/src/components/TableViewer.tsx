
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

function TableViewer() {
  const { db, table } = useParams();
  const [columns, setColumns] = useState<any[]>([]);
  const [rows, setRows] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!db || !table) return;
    Promise.all([
      fetch(`/api/databases/${db}/tables/${table}/columns`).then((res) => {
        if (!res.ok) throw new Error("Failed to fetch columns");
        return res.json();
      }),
      fetch(`/api/databases/${db}/tables/${table}`).then((res) => {
        if (!res.ok) throw new Error("Failed to fetch table content");
        return res.json();
      })
    ])
      .then(([cols, data]) => {
        setColumns(cols);
        setRows(data);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [db, table]);

  if (loading) return <div>Loading table...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="p-4 overflow-auto">
      <h1 className="text-xl font-bold mb-4">{table} in {db}</h1>
      <table className="table-auto border border-collapse w-full">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.name} className="border px-2 py-1 bg-gray-100 text-left">{col.name}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, idx) => (
            <tr key={idx}>
              {columns.map((col) => (
                <td key={col.name} className="border px-2 py-1">{row[col.name]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TableViewer;