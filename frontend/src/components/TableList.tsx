import { useEffect, useState } from "react";

interface Props {
  dbName: string;
  onSelectTable: (table: string) => void;
}

function TableList({ dbName, onSelectTable }: Props) {
  const [tables, setTables] = useState<string[]>([]);

  useEffect(() => {
    fetch(`/api/databases/${dbName}/tables`)
      .then((res) => res.json())
      .then((data) => setTables(data));
  }, [dbName]);

  return (
    <div>
      <h2 className="text-lg font-semibold">Tables in {dbName}:</h2>
      <ul className="list-disc ml-6">
        {tables.map((table) => (
          <li
            key={table}
            className="cursor-pointer text-blue-600 hover:underline"
            onClick={() => onSelectTable(table)}
          >
            {table}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TableList;
