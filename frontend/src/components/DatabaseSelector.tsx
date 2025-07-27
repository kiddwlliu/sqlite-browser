import { useEffect, useState } from "react";

interface Props {
  onSelectDb: (db: string) => void;
}

function DatabaseSelector({ onSelectDb }: Props) {
  const [databases, setDatabases] = useState<string[]>([]);

  useEffect(() => {
    fetch("/api/databases")
      .then((res) => res.json())
      .then((data) => setDatabases(data));
  }, []);

  return (
    <div>
      <label className="font-semibold">Select Database:</label>
      <select
        className="ml-2 border rounded p-1"
        onChange={(e) => onSelectDb(e.target.value)}
        defaultValue=""
      >
        <option value="" disabled>
          -- choose one --
        </option>
        {databases.map((db) => (
          <option key={db} value={db}>
            {db}
          </option>
        ))}
      </select>
    </div>
  );
}

export default DatabaseSelector;
