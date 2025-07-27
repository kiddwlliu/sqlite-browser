import { useEffect, useState } from "react";

interface Props {
  dbName: string;
  tableName: string;
}

function TableViewer({ dbName, tableName }: Props) {
  const [rows, setRows] = useState<any[]>([]);
  const [columns, setColumns] = useState<string[]>([]);

  useEffect(() => {
    fetch(`/api/databases/${dbName}/tables/${tableName}?limit=50&offset=0`)
      .then((res) => res.json())
      .then((data) => {
        if (data.length > 0) {
          setColumns(Object.keys(data[0]));
          setRows(data);
        } else {
          setColumns([]);
          setRows([]);
        }
      });
  }, [dbName, tableName]);

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Preview: {tableName}</h2>
      {rows.length === 0 ? (
        <div className="text-gray-500">No data</div>
      ) : (
        <table className="border border-gray-300 w-full text-sm">
          <thead>
            <tr className="bg-gray-200">
              {columns.map((col) => (
                <th key={col} className="border p-1">
                  {col}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, i) => (
              <tr key={i} className="even:bg-gray-50">
                {columns.map((col) => (
                  <td key={col} className="border p-1">
                    {row[col] ?? ""}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default TableViewer;
