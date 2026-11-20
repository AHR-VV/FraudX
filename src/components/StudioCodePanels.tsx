import React, { useMemo, useState } from 'react';
import {
  BACKEND_HANDOFF_DOC,
  KOTLIN_PROJECT_FILES,
  type ProjectSourceFile,
} from '../data/kotlinSourceRegistry';

export const KotlinSourceViewer: React.FC = () => {
  const [selectedPath, setSelectedPath] = useState(KOTLIN_PROJECT_FILES[0].path);
  const [copiedPath, setCopiedPath] = useState<string | null>(null);

  const selectedFile = useMemo(
    () => KOTLIN_PROJECT_FILES.find((f) => f.path === selectedPath) ?? KOTLIN_PROJECT_FILES[0],
    [selectedPath]
  );

  const grouped = useMemo(() => {
    const groups: Record<ProjectSourceFile['group'], ProjectSourceFile[]> = {
      Screens: [],
      'Components & Nav': [],
      'State & Backend Hooks': [],
      'Theme & Config': [],
    };
    KOTLIN_PROJECT_FILES.forEach((f) => {
      groups[f.group].push(f);
    });
    return groups;
  }, []);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(selectedFile.content);
      setCopiedPath(selectedFile.path);
      window.setTimeout(() => setCopiedPath(null), 1800);
    } catch {
      setCopiedPath(null);
    }
  };

  return (
    <div className="w-full max-w-[1120px] h-[min(820px,calc(100dvh-108px))] bg-white rounded-[28px] border border-[#d5e0d9] shadow-[0_18px_50px_-16px_rgba(19,51,36,0.12)] overflow-hidden grid grid-cols-1 md:grid-cols-[270px_1fr]">
      {/* File Tree Sidebar */}
      <aside className="bg-[#f4f7f5] border-r border-[#e2e8e5] overflow-y-auto p-4 flex flex-col gap-4">
        {(Object.keys(grouped) as ProjectSourceFile['group'][]).map((groupName) => (
          <div key={groupName} className="flex flex-col gap-1">
            <span className="px-2 text-[11px] font-bold uppercase tracking-wider text-[#6d7b6c]">
              {groupName}
            </span>
            {grouped[groupName].map((file) => {
              const active = file.path === selectedFile.path;
              return (
                <button
                  key={file.path}
                  type="button"
                  onClick={() => setSelectedPath(file.path)}
                  className={`w-full text-left px-3 py-2 rounded-xl text-[12.5px] font-medium transition-colors truncate cursor-pointer ${
                    active
                      ? 'bg-[#1e4635] text-white font-semibold shadow-2xs'
                      : 'text-[#2e4d3d] hover:bg-[#e6eee9]'
                  }`}
                  style={{ fontFamily: 'var(--font-mono)' }}
                >
                  {file.label}
                </button>
              );
            })}
          </div>
        ))}
      </aside>

      {/* Code Pane */}
      <div className="flex flex-col min-w-0 h-full bg-[#131d18] text-[#e6f1eb]">
        <div className="px-5 py-3.5 border-b border-white/10 flex items-center justify-between gap-4 shrink-0 bg-[#18241e]">
          <div className="flex items-center gap-2.5 min-w-0">
            <span className="material-symbols-outlined text-[18px] text-[#4ae176]">code</span>
            <span
              className="text-[12.5px] font-semibold text-white/90 truncate"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              {selectedFile.path}
            </span>
          </div>

          <button
            type="button"
            onClick={() => void handleCopy()}
            className="px-3.5 py-1.5 rounded-full bg-white/10 hover:bg-white/15 text-[12px] font-semibold text-white flex items-center gap-1.5 shrink-0 cursor-pointer transition-colors"
          >
            <span className="material-symbols-outlined text-[15px]">
              {copiedPath === selectedFile.path ? 'check' : 'content_copy'}
            </span>
            <span>{copiedPath === selectedFile.path ? 'Copied' : 'Copy file'}</span>
          </button>
        </div>

        <pre
          className="flex-1 overflow-auto p-5 text-[12.5px] leading-[1.6] text-[#d9ebe1]"
          style={{ fontFamily: 'var(--font-mono)' }}
        >
          <code>{selectedFile.content}</code>
        </pre>
      </div>
    </div>
  );
};

export const BackendHandoffViewer: React.FC = () => {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(BACKEND_HANDOFF_DOC);
      setCopied(true);
      window.setTimeout(() => setCopied(false), 1800);
    } catch {
      setCopied(false);
    }
  };

  return (
    <div className="w-full max-w-[960px] h-[min(820px,calc(100dvh-108px))] bg-white rounded-[28px] border border-[#d5e0d9] shadow-[0_18px_50px_-16px_rgba(19,51,36,0.12)] overflow-hidden flex flex-col">
      <div className="px-6 py-4 border-b border-[#e2e8e5] bg-[#f8faf9] flex items-center justify-between gap-4 shrink-0">
        <div className="flex items-center gap-2.5 min-w-0">
          <span className="material-symbols-outlined text-[20px] text-[#006e2f]">description</span>
          <div className="flex flex-col min-w-0">
            <span
              className="text-[15px] font-bold text-[#14231b] truncate"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              BACKEND_HANDOFF.md
            </span>
            <span className="text-[12px] text-[#6d7b6c] truncate">
              REST API contracts, Kotlin schemas, FraudXRepository hooks &amp; Android OS services
            </span>
          </div>
        </div>

        <button
          type="button"
          onClick={() => void handleCopy()}
          className="px-4 py-2 rounded-full bg-[#1e4635] hover:bg-[#143326] text-white text-[12px] font-bold flex items-center gap-1.5 shrink-0 cursor-pointer transition-colors"
        >
          <span className="material-symbols-outlined text-[16px]">
            {copied ? 'check' : 'content_copy'}
          </span>
          <span>{copied ? 'Copied Markdown' : 'Copy Handoff Doc'}</span>
        </button>
      </div>

      <div className="flex-1 overflow-auto p-6 bg-white">
        <pre
          className="whitespace-pre-wrap break-words text-[13px] leading-[1.68] text-[#1f2e26]"
          style={{ fontFamily: 'var(--font-mono)' }}
        >
          {BACKEND_HANDOFF_DOC}
        </pre>
      </div>
    </div>
  );
};
